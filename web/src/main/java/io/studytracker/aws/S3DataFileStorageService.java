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

package io.studytracker.aws;

import io.studytracker.aws.integration.S3IntegrationOptions;
import io.studytracker.aws.integration.S3IntegrationOptionsFactory;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3DataFileStorageService  implements DataFileStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3DataFileStorageService.class);

  private final S3Client client;

  public S3DataFileStorageService(S3Client client) {
    this.client = client;
  }

  @Override
  public StorageFolder findFolderByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();
    LOGGER.debug("Using bucket: {}", bucketName);

    // Clean the path input
    if (!path.trim().equals("") && !path.endsWith("/")) {
      path += "/";
    }

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucketName)
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
  public StorageFile findFileByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {

    LOGGER.debug("Looking up file by path: {}", path);

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucketName)
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
  public StorageFolder createFolder(FileStorageLocation location, String path, String name)
      throws StudyStorageException {
    LOGGER.info("Creating folder: '{}' in path: '{}' in bucket '{}'", name, path, location.getName());

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
  public StorageFile saveFile(FileStorageLocation location, String path, File file)
      throws StudyStorageException {

    LOGGER.info("Uploading file: {} to path: {} in bucket: {}", file.getName(), path, location.getName());

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();

    // Check permissions
    if (!StoragePermissions.canWrite(location.getPermissions())) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files");
    }

    // Cleanup the path
    String fullPath = S3Utils.joinS3Path(path, file.getName());

    // Upload the file to S3
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.fromFile(file));
    } catch (Exception e) {
      e.printStackTrace();
      throw new StudyStorageException("Failed to upload file: " + path, e);
    }
    return findFileByPath(location, fullPath);
  }

  @Override
  public Resource fetchFile(FileStorageLocation location, String path) throws StudyStorageException {

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();

    try {
      return new ByteArrayResource(
          client.getObjectAsBytes(b -> b.bucket(bucketName).key(path)).asByteArray());
    } catch (Exception e) {
      throw new StudyStorageException("Failed to download file: " + path, e);
    }
  }

  @Override
  public boolean fileExists(FileStorageLocation location, String path) {

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucketName)
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
  public boolean folderExists(FileStorageLocation location, String path) {

    // Get the bucket
    S3IntegrationOptions options = S3IntegrationOptionsFactory.create(location.getIntegrationInstance());
    String bucketName = options.getBucketName();

    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucketName)
          .prefix(path)
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return response.keyCount() > 0;
    } catch (Exception e) {
      return false;
    }


  }

}
