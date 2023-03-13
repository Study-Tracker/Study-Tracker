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
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.EgnyteDrive;
import io.studytracker.model.EgnyteDriveFolder;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.service.OrganizationService;
import io.studytracker.storage.StorageUtils;
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
  private OrganizationService organizationService;

  @Autowired
  private EgnyteStudyStorageService egnyteStudyStorageService;

  private EgnyteIntegration registerEgnyteIntegrations(Organization organization)
      throws InvalidConfigurationException {

    EgnyteIntegration egnyteIntegration = null;
    EgnyteProperties egnyteProperties = properties.getEgnyte();

    if (egnyteProperties != null
        && StringUtils.hasText(egnyteProperties.getTenantName())
        && StringUtils.hasText(egnyteProperties.getApiToken())) {

      // Check to see if the integration already exists
      List<EgnyteIntegration> integrations = egnyteIntegrationService.findByOrganization(organization);

      // If yes, update the record
      if (integrations.size() > 0) {
        LOGGER.info("Updating Egnyte integration for organization {}", organization.getName());
        EgnyteIntegration existing = integrations.get(0);
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
        LOGGER.info("Creating new Egnyte integration for organization {}", organization.getName());
        EgnyteIntegration newIntegration = new EgnyteIntegration();
        newIntegration.setOrganization(organization);
        newIntegration.setTenantName(egnyteProperties.getTenantName());
        newIntegration.setRootUrl("https://" + egnyteProperties.getTenantName() + ".egnyte.com");
        newIntegration.setApiToken(egnyteProperties.getApiToken());
        newIntegration.setQps(egnyteProperties.getQps());
        egnyteIntegration = egnyteIntegrationService.register(newIntegration);
      }

    }

    return egnyteIntegration;

  }

  private EgnyteDrive registerEgnyteDrives(EgnyteIntegration egnyteIntegration) {
    EgnyteDrive defaultDrive = egnyteIntegrationService.listIntegrationDrives(egnyteIntegration)
        .stream()
        .filter(d -> d.getName().equals("Shared"))
        .findFirst()
        .orElseGet(null);
    if (defaultDrive == null) {
      LOGGER.info("Registering Egnyte default drive for integration {}", egnyteIntegration.getTenantName());
      defaultDrive = egnyteIntegrationService.registerDefaultDrive(egnyteIntegration);
    }
    return defaultDrive;
  }

  private void registerEgnyteFolders(EgnyteDrive egnyteDrive) {



    EgnyteProperties egnyteProperties = properties.getEgnyte();
    String rootPath = egnyteProperties.getRootPath();
    String rootFolderName = StorageUtils.getFolderNameFromPath(rootPath);

    StorageDriveFolder rootFolder = new StorageDriveFolder();
    rootFolder.setStorageDrive(egnyteDrive.getStorageDrive());
    rootFolder.setPath(rootPath);
    rootFolder.setName(rootFolderName);
    rootFolder.setBrowserRoot(true);
    rootFolder.setStudyRoot(true);
    rootFolder.setWriteEnabled(true);

    EgnyteDriveFolder egnyteRootFolder = new EgnyteDriveFolder();
    egnyteRootFolder.setEgnyteDrive(egnyteDrive);
    egnyteRootFolder.setStorageDriveFolder(rootFolder);

    egnyteIntegrationService.registerFolder(egnyteRootFolder);



  }

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    Organization organization;

    // Check to see if the AWS integration is already registered
    try {
      organization = organizationService.getCurrentOrganization();
    } catch (RecordNotFoundException e) {
      e.printStackTrace();
      LOGGER.warn("No organization found. Skipping Egnyte integration initialization.");
      return;
    }

    try {
      // Register Egnyte integration
      EgnyteIntegration egnyteIntegration = registerEgnyteIntegrations(organization);
      if (egnyteIntegration != null) {
        EgnyteDrive drive = registerEgnyteDrives(egnyteIntegration);

      }

    } catch (Exception e) {
      LOGGER.error("Failed to initialize Egnyte integrations", e);
      e.printStackTrace();
      throw new InvalidConfigurationException(e);
    }

  }

}
