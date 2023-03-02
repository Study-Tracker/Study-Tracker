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

import io.studytracker.aws.S3DataFileStorageService;
import io.studytracker.egnyte.EgnyteApiDataFileStorageService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Helper class for determining the correct {@link DataFileStorageService} implementation for a
 *   given request.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

//@Component
@Deprecated
public class DataFileStorageServiceLookup {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFileStorageServiceLookup.class);

  @Autowired(required = false)
  private EgnyteApiDataFileStorageService egnyteApiDataFileStorageService;

  @Autowired(required = false)
  private S3DataFileStorageService s3DataFileStorageService;

  @Autowired(required = false)
  private LocalFileSystemStorageService localFileSystemStorageService;

  public Optional<DataFileStorageService> lookup(StorageLocationType storageLocationType) {
    LOGGER.debug("Looking up DataFileStorageService for storageLocationType: {}", storageLocationType);
    switch (storageLocationType) {
      case EGNYTE_API:
        return Optional.ofNullable(egnyteApiDataFileStorageService);
      case AWS_S3:
        return Optional.ofNullable(s3DataFileStorageService);
      case LOCAL_FILE_SYSTEM:
        return Optional.ofNullable(localFileSystemStorageService);
      default:
        return Optional.empty();
    }
  }

}
