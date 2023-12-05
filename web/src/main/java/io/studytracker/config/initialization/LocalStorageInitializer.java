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
import io.studytracker.model.LocalDriveDetails;
import io.studytracker.model.LocalDriveFolderDetails;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Optional;

@Component
public class LocalStorageInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorageInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private StorageDriveFolderRepository folderRepository;

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    StorageProperties storageProperties = properties.getStorage();

    if (storageProperties.getMode().equals("local") && StringUtils.hasText(storageProperties.getLocalDir())) {
      
      // Make sure the local directory exists and is writable
      File file = new File(storageProperties.getLocalDir());
      if (!file.exists()) {
        if (!file.mkdirs()) {
          throw new InvalidConfigurationException("Unable to create local storage directory: "
              + storageProperties.getLocalDir());
        }
      } else if (!file.canWrite()) {
        throw new InvalidConfigurationException("Local storage directory is not writable: "
            + storageProperties.getLocalDir());
      }

      Optional<StorageDrive> optional = storageDriveRepository.findByDriveType(DriveType.LOCAL)
          .stream()
          .filter(d -> d.getRootPath().equals(storageProperties.getLocalDir()))
          .findFirst();
      StorageDrive storageDrive;

      if (optional.isPresent()) {
        storageDrive = storageDriveRepository.getById(optional.get().getId());
        storageDrive.setRootPath(storageProperties.getLocalDir());
        storageDriveRepository.save(storageDrive);
      } else {
        storageDrive = new StorageDrive();
        storageDrive.setRootPath(storageProperties.getLocalDir());
        storageDrive.setDisplayName("Default Local Drive");
        storageDrive.setDriveType(DriveType.LOCAL);
        storageDrive.setActive(true);

        LocalDriveDetails localDrive = new LocalDriveDetails();
        localDrive.setName("Default Local Drive");
        storageDrive.setDetails(localDrive);
        storageDriveRepository.save(storageDrive);

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
      rootFolder.setDetails(new LocalDriveFolderDetails());

      folderRepository.save(rootFolder);

    }

  }

}
