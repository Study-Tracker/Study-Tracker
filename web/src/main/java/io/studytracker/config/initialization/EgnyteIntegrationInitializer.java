/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.config.initialization;

import io.studytracker.config.properties.EgnyteProperties;
import io.studytracker.config.properties.StudyTrackerProperties;
import io.studytracker.egnyte.EgnyteIntegrationService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.model.EgnyteDriveDetails;
import io.studytracker.model.EgnyteFolderDetails;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class EgnyteIntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteIntegrationInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private EgnyteIntegrationService egnyteIntegrationService;

  @Autowired
  private EgnyteStudyStorageService egnyteStudyStorageService;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private StorageDriveFolderRepository storageDriveFolderRepository;

  private EgnyteIntegration registerEgnyteIntegrations()
      throws InvalidConfigurationException {

    LOGGER.info("Initializing Egnyte integrations");
    EgnyteIntegration egnyteIntegration = null;
    EgnyteProperties egnyteProperties = properties.getEgnyte();

    if (egnyteProperties != null
        && StringUtils.hasText(egnyteProperties.getTenantName())
        && StringUtils.hasText(egnyteProperties.getApiToken())) {

      // Check to see if the integration already exists
      List<EgnyteIntegration> integrations = egnyteIntegrationService.findAll();

      // If yes, update the record
      if (integrations.size() > 0) {

        // Check to see if the integration is already active and updated
        EgnyteIntegration existing = integrations.get(0);
        if (!existing.getCreatedAt().equals(existing.getUpdatedAt())) {
          LOGGER.info("Egnyte integration is already active");
          return existing;
        }

        // If not, update the record
        LOGGER.info("Updating Egnyte integration");
        existing.setTenantName(egnyteProperties.getTenantName());
        existing.setApiToken(egnyteProperties.getApiToken());
        if (StringUtils.hasText(egnyteProperties.getRootUrl())) {
          existing.setRootUrl(egnyteProperties.getRootUrl());
        } else {
          existing.setRootUrl("https://" + egnyteProperties.getTenantName() + ".egnyte.com");
        }
        if (egnyteProperties.getQps() != null) {
          existing.setQps(egnyteProperties.getQps());
        } else {
          existing.setQps(1);
        }
        egnyteIntegration = egnyteIntegrationService.update(existing);
      }

      // If no, create a new integration
      else {
        LOGGER.info("Creating new Egnyte integration");
        EgnyteIntegration newIntegration = new EgnyteIntegration();
        newIntegration.setTenantName(egnyteProperties.getTenantName());
        newIntegration.setRootUrl("https://" + egnyteProperties.getTenantName() + ".egnyte.com");
        newIntegration.setApiToken(egnyteProperties.getApiToken());
        newIntegration.setQps(egnyteProperties.getQps());
        newIntegration.setActive(true);
        egnyteIntegration = egnyteIntegrationService.register(newIntegration);
      }

    } else {
      LOGGER.info("Egnyte integration properties are not set");
    }

    return egnyteIntegration;

  }

  private StorageDrive registerEgnyteDrives(EgnyteIntegration egnyteIntegration) {
    StorageDrive defaultDrive = egnyteIntegrationService.listIntegrationDrives(egnyteIntegration)
        .stream()
        .filter(d -> ((EgnyteDriveDetails) d.getDetails()).getName().equals("Shared"))
        .findFirst()
        .orElse(null);
    if (defaultDrive == null) {
      LOGGER.info("Registering Egnyte default drive for integration {}", egnyteIntegration.getTenantName());
      defaultDrive = egnyteIntegrationService.registerDefaultDrive(egnyteIntegration);
    }
    return defaultDrive;
  }

  private void registerEgnyteFolders(StorageDrive drive) {

    // Does the drive already have a root folder?
    boolean hasRoot = storageDriveFolderService.findStudyRootFolders().stream()
        .anyMatch(f -> f.getStorageDrive().getDriveType().equals(DriveType.EGNYTE));
    if (!hasRoot) {

      EgnyteProperties egnyteProperties = properties.getEgnyte();
      String rootPath = egnyteProperties.getRootPath();

      // Does the folder exist already?
      StorageFolder storageFolder = null;
      boolean folderExists = egnyteStudyStorageService.folderExists(drive, rootPath);
      // If yes, register it in the database
      if (folderExists) {
        try {
          storageFolder = egnyteStudyStorageService.findFolderByPath(drive, rootPath);
        } catch (StudyStorageNotFoundException e) {
          e.printStackTrace();
          LOGGER.error("Could not find folder {} in Egnyte drive {}", rootPath, drive.getId());
        }
      }
      // If not, create the folder
      else {
        try {
          String rootFolder = StorageUtils.getFolderNameFromPath(rootPath);
          String parentPath = StorageUtils.getParentPathFromPath(rootPath);
          storageFolder = egnyteStudyStorageService.createFolder(drive, parentPath, rootFolder);
        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.error("Could not create folder {} in Egnyte drive {}", rootPath, drive.getId());
        }
      }

      // Persist the record
      if (storageFolder != null) {
        StorageDriveFolder storageDriveFolder = new StorageDriveFolder();
        storageDriveFolder.setStorageDrive(drive);
        storageDriveFolder.setName(storageFolder.getName());
        storageDriveFolder.setPath(storageFolder.getPath());
        storageDriveFolder.setBrowserRoot(true);
        storageDriveFolder.setStudyRoot(true);
        storageDriveFolder.setWriteEnabled(true);
        storageDriveFolder.setDeleteEnabled(false);

        EgnyteFolderDetails egnyteFolderDetails = new EgnyteFolderDetails();
        egnyteFolderDetails.setFolderId(storageFolder.getFolderId());
        egnyteFolderDetails.setWebUrl(storageFolder.getUrl());
        storageDriveFolder.setDetails(egnyteFolderDetails);

        storageDriveFolderRepository.save(storageDriveFolder);

      } else {
        LOGGER.warn("Could not register Egnyte root folder {} in drive {}", rootPath, drive.getId());
      }

    }

  }

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    try {
      // Register Egnyte integration
      EgnyteIntegration egnyteIntegration = registerEgnyteIntegrations();
      if (egnyteIntegration != null) {
        StorageDrive drive = registerEgnyteDrives(egnyteIntegration);
        registerEgnyteFolders(drive);
        LOGGER.info("Egnyte integration initialized successfully.");
      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize Egnyte integrations", e);
      throw new InvalidConfigurationException(e);
    }

  }

}
