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
import io.studytracker.model.Organization;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.LocalDriveRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
import java.util.Optional;
import javax.annotation.PostConstruct;
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

  @Autowired private LocalDriveRepository localDriveRepository;
  @Autowired private OrganizationService organizationService;
  @Autowired private StorageDriveRepository storageDriveRepository;

  @PostConstruct
  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    StorageProperties storageProperties = properties.getStorage();

    if (storageProperties.getMode().equals("local") && StringUtils.hasText(storageProperties.getLocalDir())) {

      Organization organization;

      // Check to see if the AWS integration is already registered
      try {
        organization = organizationService.getCurrentOrganization();
      } catch (RecordNotFoundException e) {
        e.printStackTrace();
        LOGGER.warn("No organization found. Skipping Egnyte integration initialization.");
        return;
      }

      Optional<LocalDrive> optional = localDriveRepository.findByOrganizationId(organization.getId())
          .stream()
          .filter(d -> d.getOrganization().getId().equals(organization.getId())
              && d.getStorageDrive().getRootPath().equals(storageProperties.getLocalDir()))
          .findFirst();

      if (optional.isPresent()) {
        LocalDrive localDrive = optional.get();
        StorageDrive d = storageDriveRepository.getById(localDrive.getStorageDrive().getId());
        d.setRootPath(storageProperties.getLocalDir());
        storageDriveRepository.save(d);
      } else {
        StorageDrive drive = new StorageDrive();
        drive.setOrganization(organization);
        drive.setRootPath(storageProperties.getLocalDir());
        drive.setDisplayName("Default Local Drive");
        drive.setDriveType(DriveType.LOCAL);
        drive.setActive(true);

        LocalDrive localDrive = new LocalDrive();
        localDrive.setStorageDrive(drive);
        localDrive.setName("Default Local Drive");
        localDrive.setOrganization(organization);
        localDriveRepository.save(localDrive);
      }

    }

  }

}
