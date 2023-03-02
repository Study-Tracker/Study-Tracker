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

package io.studytracker.aws;

import io.studytracker.aws.integration.S3IntegrationOptions;
import io.studytracker.aws.integration.S3IntegrationOptionsFactory;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.S3BucketFolderRepository;
import io.studytracker.repository.S3BucketRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3StudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3StudyStorageService.class);

  @Autowired
  private S3BucketRepository s3BucketRepository;

  @Autowired
  private S3BucketFolderRepository s3BucketFolderRepository;

  @Autowired
  private AwsIntegrationRepository integrationRepository;

  private S3Client getClientFromFolder(StorageDriveFolder folder) {

  }

  @Override
  public StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program)
      throws StudyStorageException {
    return null;
  }

  @Override
  public StorageDriveFolder createStudyFolder(StorageDriveFolder parentFolder, Study study)
      throws StudyStorageException {
    return null;
  }

  @Override
  public StorageDriveFolder createAssayFolder(StorageDriveFolder parentFolder, Assay assay)
      throws StudyStorageException {
    return null;
  }

  @Override
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: '{}' in path: '{}' in bucket '{}'", name, path, parentFolder.getName());

    String fullPath = S3Utils.joinS3Path(path, name) + "/";
    try {

      // Get the bucket
      S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
      String bucketName = options.getBucketName();

      if (!StoragePermissions.canWrite(location.getPermissions())) {
        throw new InsufficientPrivilegesException("Insufficient privileges to create folder: " + fullPath);
      }

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.empty());

    } catch (Exception e) {
      throw new StudyStorageException("Failed to create folder: " + path, e);
    }
    return findFolderByPath(location, fullPath);
  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFile findFileByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    return null;
  }

  @Override
  public StorageFile saveFile(StorageDriveFolder folder, String path, File file)
      throws StudyStorageException {
    return null;
  }

  @Override
  public Resource fetchFile(StorageDriveFolder folder, String path) throws StudyStorageException {
    return null;
  }

  @Override
  public boolean fileExists(StorageDriveFolder folder, String path) {
    return false;
  }

  @Override
  public boolean folderExists(StorageDriveFolder folder, String path) {
    return false;
  }
}
