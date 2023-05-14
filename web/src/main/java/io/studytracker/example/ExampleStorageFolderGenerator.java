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

package io.studytracker.example;

import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.EgnyteDriveFolderRepository;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.LocalDriveFolderRepository;
import io.studytracker.repository.LocalDriveRepository;
import io.studytracker.repository.OneDriveDriveRepository;
import io.studytracker.repository.OneDriveFolderRepository;
import io.studytracker.repository.S3BucketFolderRepository;
import io.studytracker.repository.S3BucketRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleStorageFolderGenerator implements ExampleDataGenerator<StorageDriveFolder> {

  @Autowired private StorageDriveRepository storageDriveRepository;
  @Autowired private StorageDriveFolderRepository storageDriveFolderRepository;
  @Autowired private EgnyteDriveRepository egnyteDriveRepository;
  @Autowired private EgnyteDriveFolderRepository egnyteDriveFolderRepository;
  @Autowired private OneDriveDriveRepository oneDriveDriveRepository;
  @Autowired private OneDriveFolderRepository oneDriveFolderRepository;
  @Autowired private S3BucketRepository s3BucketRepository;
  @Autowired private S3BucketFolderRepository s3BucketFolderRepository;
  @Autowired private LocalDriveRepository localDriveRepository;
  @Autowired private LocalDriveFolderRepository localDriveFolderRepository;

  @Override
  public List<StorageDriveFolder> generateData(Object... args) throws Exception {
    return null;
  }

  @Override
  public void deleteData() {
    localDriveFolderRepository.deleteAll();
    s3BucketFolderRepository.deleteAll();
    egnyteDriveFolderRepository.deleteAll();
    oneDriveFolderRepository.deleteAll();
    storageDriveFolderRepository.deleteAll();
    localDriveRepository.deleteAll();
    egnyteDriveRepository.deleteAll();
    s3BucketRepository.deleteAll();
    oneDriveDriveRepository.deleteAll();
    storageDriveRepository.deleteAll();
  }
}
