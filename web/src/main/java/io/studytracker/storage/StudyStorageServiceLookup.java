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

package io.studytracker.storage;

import io.studytracker.aws.S3StudyStorageService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.msgraph.OneDriveStorageService;
import io.studytracker.repository.StorageDriveRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Helper class for determining the correct {@link StudyStorageService} implementation for a
 *   given request.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Component
public class StudyStorageServiceLookup {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyStorageServiceLookup.class);

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Autowired
  private ApplicationContext context;

  public Optional<StudyStorageService> lookup(StorageDriveFolder folder) {
    LOGGER.debug("Looking up StudyStorageService for storageDriveFolder: {}", folder);
    StorageDrive drive = storageDriveRepository.findById(folder.getStorageDrive().getId())
        .orElseThrow(() -> new RecordNotFoundException("StorageDrive not found for id: "
            + folder.getStorageDrive().getId()));
    return lookup(drive.getDriveType());
  }

  public Optional<StudyStorageService> lookup(DriveType driveType) {
    LOGGER.debug("Looking up StudyStorageService for storageLocationType: {}", driveType);
    switch (driveType) {
      case EGNYTE:
        return Optional.of(context.getBean(EgnyteStudyStorageService.class));
      case S3:
        return Optional.of(context.getBean(S3StudyStorageService.class));
      case LOCAL:
        return Optional.of(context.getBean(LocalFileSystemStorageService.class));
      case ONEDRIVE:
        return Optional.of(context.getBean(OneDriveStorageService.class));
      default:
        return Optional.empty();
    }
  }

}
