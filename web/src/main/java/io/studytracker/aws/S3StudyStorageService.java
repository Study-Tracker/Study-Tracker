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

import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Program;
import io.studytracker.model.S3Bucket;
import io.studytracker.model.S3BucketFolder;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.S3BucketFolderRepository;
import io.studytracker.repository.S3BucketRepository;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3StudyStorageService implements StudyStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3StudyStorageService.class);

  @Autowired
  private S3BucketRepository s3BucketRepository;

  @Autowired
  private S3BucketFolderRepository s3BucketFolderRepository;

  @Autowired
  private AwsIntegrationRepository integrationRepository;

  private S3Client getClientFromDrive(StorageDrive drive) {
    AwsIntegration integration = integrationRepository
        .findByStorageDriveFolderId(drive.getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + drive.getId()
            + " not associated with AWS integration"));
    return AWSClientFactory.createS3Client(integration);

  }

  @Override
  public StorageDriveFolder createProgramFolder(StorageDriveFolder parentFolder, Program program)
      throws StudyStorageException {
    LOGGER.info("Creating program folder: '{}' in bucket '{}'", program.getName(), parentFolder.getName());
    String folderName = S3Utils.generateProgramFolderName(program);
    StorageFolder storageFolder = this.createFolder(parentFolder, parentFolder.getPath(), folderName);
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);
  }

  @Override
  public StorageDriveFolder createStudyFolder(StorageDriveFolder parentFolder, Study study)
      throws StudyStorageException {
    LOGGER.info("Creating study folder: '{}' in bucket '{}'", study.getName(), parentFolder.getName());
    String folderName = S3Utils.generateStudyFolderName(study);
    StorageFolder storageFolder = this.createFolder(parentFolder, parentFolder.getPath(), folderName);
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);
  }

  @Override
  public StorageDriveFolder createAssayFolder(StorageDriveFolder parentFolder, Assay assay)
      throws StudyStorageException {
    LOGGER.info("Creating assay folder: '{}' in bucket '{}'", assay.getName(), parentFolder.getName());
    String folderName = S3Utils.generateAssayFolderName(assay);
    StorageFolder storageFolder = this.createFolder(parentFolder, parentFolder.getPath(), folderName);
    StorageDriveFolder options = new StorageDriveFolder();
    options.setWriteEnabled(true);
    return saveStorageFolderRecord(parentFolder.getStorageDrive(), storageFolder, options);
  }

  @Override
  public StorageFolder createFolder(StorageDrive drive, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: '{}' in path: '{}' in bucket '{}'", name, path, drive.getDisplayName());

    S3Client client = getClientFromDrive(drive);
    String fullPath = S3Utils.joinS3Path(path, name) + "/";
    S3Bucket bucket;
    try {

      // Get the bucket
      bucket = s3BucketRepository.findByStorageDriveId(drive.getId())
          .orElseThrow(() -> new RecordNotFoundException("Storage drive " + drive.getId()
              + " not associated with S3 bucket"));

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket.getName())
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.empty());

    } catch (Exception e) {
      throw new StudyStorageException("Failed to create folder: " + path, e);
    }

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return S3Utils.convertS3ObjectsToStorageFolderWithContents(path, response.contents(),
          response.commonPrefixes());
    } catch (AwsServiceException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException("Cannot access folder at path: " + path);
    }
  }

  @Override
  public StorageFolder createFolder(StorageDriveFolder parentFolder, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: '{}' in path: '{}' in bucket '{}'", name, path, parentFolder.getName());

    S3Client client = getClientFromDrive(parentFolder.getStorageDrive());
    String fullPath = S3Utils.joinS3Path(path, name) + "/";
    S3Bucket bucket;
    try {

      // Get the bucket
      bucket = s3BucketRepository.findByStorageDriveFolderId(parentFolder.getId())
          .orElseThrow(() -> new RecordNotFoundException("Storage folder " + parentFolder.getId()
              + " not associated with S3 bucket"));

      if (!parentFolder.isWriteEnabled()) {
        throw new InsufficientPrivilegesException("Insufficient privileges to create folder: " + fullPath);
      }

      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket.getName())
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.empty());

    } catch (Exception e) {
      throw new StudyStorageException("Failed to create folder: " + path, e);
    }

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return S3Utils.convertS3ObjectsToStorageFolderWithContents(path, response.contents(),
          response.commonPrefixes());
    } catch (AwsServiceException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException("Cannot access folder at path: " + path);
    }
  }

  @Override
  public StorageFolder findFolderByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return findFolderByPath(parentFolder.getStorageDrive(), path);
  }

  @Override
  public StorageFolder findFolderByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);

    S3Client client = getClientFromDrive(drive);
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(drive.getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + drive.getId()
            + " not associated with S3 bucket"));

    // Clean the path input
    if (!path.trim().equals("") && !path.endsWith("/")) {
      path += "/";
    }

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      LOGGER.debug("Found {} files and {} folders in path {}", response.contents().size(),
          response.commonPrefixes().size(), path);
      LOGGER.debug(response.toString());
      return S3Utils.convertS3ObjectsToStorageFolderWithContents(path, response.contents(),
          response.commonPrefixes());
    } catch (AwsServiceException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException("Cannot access folder at path: " + path);
    }
  }

  @Override
  public StorageFile findFileByPath(StorageDriveFolder parentFolder, String path)
      throws StudyStorageNotFoundException {
    return findFileByPath(parentFolder.getStorageDrive(), path);
  }

  @Override
  public StorageFile findFileByPath(StorageDrive drive, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up file by path: {}", path);

    S3Client client = getClientFromDrive(drive);
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(drive.getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + drive.getId()
            + " not associated with S3 bucket"));

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      S3Object s3Object = response.contents().stream().findFirst()
          .orElseThrow(
              () -> new StudyStorageNotFoundException("Failed to lookup file by path: " + path));
      if (s3Object.key().endsWith("/")) {
        throw new StudyStorageNotFoundException("Object at path is a folder: " + path);
      }
      return S3Utils.convertS3ObjectToStorageFile(s3Object);
    } catch (AwsServiceException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException("Cannot access file at path: " + path);
    }
  }

  @Override
  public StorageFile saveFile(StorageDriveFolder folder, String path, File file)
      throws StudyStorageException {
    LOGGER.info("Uploading file: {} to path: {} in bucket: {}", file.getName(), path, folder.getName());

    S3Client client = getClientFromDrive(folder.getStorageDrive());
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(folder.getStorageDrive().getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + folder.getId()
            + " not associated with S3 bucket"));

    // Check permissions
    if (!folder.isWriteEnabled()) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files");
    }

    // Cleanup the path
    String fullPath = S3Utils.joinS3Path(path, file.getName());

    // Upload the file to S3
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket.getName())
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.fromFile(file));
    } catch (Exception e) {
      e.printStackTrace();
      throw new StudyStorageException("Failed to upload file: " + path, e);
    }

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      S3Object s3Object = response.contents().stream().findFirst()
          .orElseThrow(
              () -> new StudyStorageNotFoundException("Failed to lookup file by path: " + path));
      if (s3Object.key().endsWith("/")) {
        throw new StudyStorageNotFoundException("Object at path is a folder: " + path);
      }
      return S3Utils.convertS3ObjectToStorageFile(s3Object);
    } catch (AwsServiceException e) {
      e.printStackTrace();
      throw new StudyStorageNotFoundException("Cannot access file at path: " + path);
    }

  }

  @Override
  public Resource fetchFile(StorageDriveFolder folder, String path) throws StudyStorageException {
    LOGGER.debug("Fetching file: {} from bucket: {}", path, folder.getName());
    S3Client client = getClientFromDrive(folder.getStorageDrive());
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(folder.getStorageDrive().getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + folder.getId()
            + " not associated with S3 bucket"));
    try {
      return new ByteArrayResource(
          client.getObjectAsBytes(b -> b.bucket(bucket.getName()).key(path)).asByteArray());
    } catch (Exception e) {
      throw new StudyStorageException("Failed to download file: " + path, e);
    }
  }

  @Override
  public boolean fileExists(StorageDrive drive, String path) {
    LOGGER.debug("Checking if file exists: {} in bucket: {}", path, drive.getDisplayName());
    S3Client client = getClientFromDrive(drive);
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(drive.getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage folder " + drive.getId()
            + " not associated with S3 bucket"));
    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return response.contents().size() > 0;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean fileExists(StorageDriveFolder folder, String path) {
    return this.fileExists(folder.getStorageDrive(), path);
  }

  @Override
  public boolean folderExists(StorageDrive drive, String path) {
    LOGGER.debug("Checking if folder exists: {} in bucket: {}", path, drive.getDisplayName());
    S3Client client = getClientFromDrive(drive);
    S3Bucket bucket = s3BucketRepository.findByStorageDriveId(drive.getId())
        .orElseThrow(() -> new RecordNotFoundException("Storage drive " + drive.getId()
            + " not associated with S3 bucket"));
    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucket.getName())
          .prefix(path)
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return response.keyCount() > 0;
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public boolean folderExists(StorageDriveFolder folder, String path) {
    return this.folderExists(folder.getStorageDrive(), path);
  }

  @Override
  @Transactional
  public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder,
      StorageDriveFolder options) {

    Optional<S3Bucket> optional = s3BucketRepository.findByStorageDriveId(drive.getId());
    if (optional.isEmpty()) {
      throw new InvalidRequestException("Egnyte drive not found.");
    }
    S3Bucket bucket = optional.get();

    StorageDriveFolder storageDriveFolder = new StorageDriveFolder();
    storageDriveFolder.setStorageDrive(drive);
    storageDriveFolder.setName(storageFolder.getName());
    storageDriveFolder.setPath(storageFolder.getPath());
    storageDriveFolder.setBrowserRoot(options.isBrowserRoot());
    storageDriveFolder.setDeleteEnabled(options.isDeleteEnabled());
    storageDriveFolder.setStudyRoot(options.isStudyRoot());
    storageDriveFolder.setWriteEnabled(options.isWriteEnabled());

    S3BucketFolder bucketFolder = new S3BucketFolder();
    bucketFolder.setS3Bucket(bucket);
    bucketFolder.setStorageDriveFolder(storageDriveFolder);
    // TODO: set etag and key

    s3BucketFolderRepository.save(bucketFolder);
    return bucketFolder.getStorageDriveFolder();
  }

  @Override public StorageDriveFolder saveStorageFolderRecord(StorageDrive drive, StorageFolder storageFolder) {
    return this.saveStorageFolderRecord(drive, storageFolder, new StorageDriveFolder());
  }

}
