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

import io.studytracker.exception.InvalidRequestException;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolderDetails;
import io.studytracker.repository.EgnyteDriveFolderRepository;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.LocalDriveFolderRepository;
import io.studytracker.repository.LocalDriveRepository;
import io.studytracker.repository.S3BucketFolderRepository;
import io.studytracker.repository.S3BucketRepository;
import io.studytracker.repository.StorageDriveFolderDetailsOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StorageDriveRepositoryLookup {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDriveRepositoryLookup.class);

  private final ApplicationContext context;

  public StorageDriveRepositoryLookup(ApplicationContext context) {
    this.context = context;
  }

  public Object lookupDriveRepository(DriveType driveType) {
    LOGGER.debug("Looking up drive repository for type: {}", driveType);
    switch (driveType) {
      case EGNYTE:
        return context.getBean(EgnyteDriveRepository.class);
      case S3:
        return context.getBean(S3BucketRepository.class);
      case LOCAL:
        return context.getBean(LocalDriveRepository.class);
      default:
        throw new InvalidRequestException("Invalid drive type: " + driveType);
    }
  }

  public StorageDriveFolderDetailsOperations<? extends StorageDriveFolderDetails> lookupFolderRepository(DriveType driveType) {
    LOGGER.debug("Looking up drive repository for type: {}", driveType);
    switch (driveType) {
      case EGNYTE:
        return context.getBean(EgnyteDriveFolderRepository.class);
      case S3:
        return context.getBean(S3BucketFolderRepository.class);
      case LOCAL:
        return context.getBean(LocalDriveFolderRepository.class);
      default:
        throw new InvalidRequestException("Invalid drive type: " + driveType);
    }
  }

}
