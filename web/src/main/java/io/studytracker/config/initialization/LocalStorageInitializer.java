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

import io.studytracker.config.properties.StorageProperties;
import io.studytracker.config.properties.StudyTrackerProperties;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.LocalDrive;
import io.studytracker.model.LocalDriveFolder;
import io.studytracker.model.Organization;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.LocalDriveFolderRepository;
import io.studytracker.repository.LocalDriveRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageUtils;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class LocalStorageInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private LocalDriveRepository localDriveRepository;

  @Autowired
  private LocalDriveFolderRepository localDriveFolderRepository;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    StorageProperties storageProperties = properties.getStorage();

    if (storageProperties.getMode().equals("local") && StringUtils.hasText(storageProperties.getLocalDir())) {

      Organization organization;
      LocalDrive localDrive;
      StorageDrive storageDrive;

      // Check to see if the AWS integration is already registered
      try {
        organization = organizationService.getCurrentOrganization();
      } catch (RecordNotFoundException e) {
        e.printStackTrace();
        LOGGER.warn("No organization found. Skipping local storage initialization.");
        return;
      }

      Optional<LocalDrive> optional = localDriveRepository.findByOrganizationId(organization.getId())
          .stream()
          .filter(d -> d.getOrganization().getId().equals(organization.getId())
              && d.getStorageDrive().getRootPath().equals(storageProperties.getLocalDir()))
          .findFirst();

      if (optional.isPresent()) {
        localDrive = optional.get();
        storageDrive = storageDriveRepository.getById(localDrive.getStorageDrive().getId());
        storageDrive.setRootPath(storageProperties.getLocalDir());
        storageDriveRepository.save(storageDrive);
      } else {
        storageDrive = new StorageDrive();
        storageDrive.setOrganization(organization);
        storageDrive.setRootPath(storageProperties.getLocalDir());
        storageDrive.setDisplayName("Default Local Drive");
        storageDrive.setDriveType(DriveType.LOCAL);
        storageDrive.setActive(true);

        localDrive = new LocalDrive();
        localDrive.setStorageDrive(storageDrive);
        localDrive.setName("Default Local Drive");
        localDrive.setOrganization(organization);
        localDriveRepository.save(localDrive);
      }

      // If root folders do not exist, create them
      if (storageDriveFolderService.findStudyRootFolders().size() > 0) {
        LOGGER.info("Root folders already exist. Skipping local storage initialization.");
        return;
      }

      StorageDriveFolder rootFolder = new StorageDriveFolder();
      rootFolder.setStudyRoot(true);
      rootFolder.setWriteEnabled(true);
      rootFolder.setBrowserRoot(true);
      rootFolder.setPath(storageProperties.getLocalDir());
      rootFolder.setName(StorageUtils.getFolderNameFromPath(storageProperties.getLocalDir()));
      rootFolder.setStorageDrive(storageDrive);

      LocalDriveFolder localDriveFolder = new LocalDriveFolder();
      localDriveFolder.setLocalDrive(localDrive);
      localDriveFolder.setStorageDriveFolder(rootFolder);

      localDriveFolderRepository.save(localDriveFolder);

    }

  }

}
