/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.aws.S3StudyFileStorageService;
import io.studytracker.egnyte.EgnyteStudyStorageService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for determining the correct {@link StudyStorageService} implementation for a
 *   given request.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Component
public class StudyFileStorageServiceLookup {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyFileStorageServiceLookup.class);

  @Autowired(required = false)
  private EgnyteStudyStorageService egnyteStudyStorageService;

  @Autowired(required = false)
  private S3StudyFileStorageService s3StudyFileStorageService;

  @Autowired(required = false)
  private LocalFileSystemStorageService localFileSystemStorageService;

  public Optional<StudyStorageService> lookup(StorageLocationType storageLocationType) {
    LOGGER.debug("Looking up StudyStorageService for storageLocationType: {}", storageLocationType);
    switch (storageLocationType) {
      case EGNYTE_API:
        return Optional.ofNullable(egnyteStudyStorageService);
      case AWS_S3:
        return Optional.ofNullable(s3StudyFileStorageService);
      case LOCAL_FILE_SYSTEM:
        return Optional.ofNullable(localFileSystemStorageService);
      default:
        return Optional.empty();
    }
  }

}
