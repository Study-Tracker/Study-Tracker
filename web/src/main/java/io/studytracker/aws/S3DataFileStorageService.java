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

import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
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
    String bucketName = (String) location.getIntegrationInstance().findConfigurationValue("bucket")
        .orElseThrow(() -> new StudyStorageNotFoundException("Missing bucket configuration for location: " + location.getId()));
    LOGGER.debug("Using bucket: {}", bucketName);

    // CLean the path input
    if (!path.trim().equals("") && !path.endsWith("/")) {
      path += "/";
    }
    if (!exists(bucketName, path)) {
      throw new StudyStorageNotFoundException("Folder not found: " + path);
    }
    LOGGER.debug("Folder '{}' exists in bucket: {}", path, bucketName);

    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix(path)
        .delimiter("/")
        .build();
    ListObjectsV2Response response = client.listObjectsV2(request);
    LOGGER.debug("Found {} objects in path {}", response.contents().size(), path);
    return S3Utils.convertS3ObjectsToStorageFolderWithContents(path, response.contents(), response.commonPrefixes());
  }

  @Override
  public StorageFile findFileByPath(FileStorageLocation location, String path)
      throws StudyStorageNotFoundException {

    // Get the bucket
    String bucketName = (String) location.getIntegrationInstance().findConfigurationValue("bucket")
        .orElseThrow(() -> new StudyStorageNotFoundException("Missing bucket configuration for location: " + location.getId()));

    if (!exists(bucketName, path)) {
      throw new StudyStorageNotFoundException("File not found: " + path);
    }
    ListObjectsV2Request request = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .prefix(path)
        .delimiter("/")
        .build();
    ListObjectsV2Response response = client.listObjectsV2(request);
    S3Object s3Object = response.contents().stream().findFirst()
        .orElseThrow(() -> new StudyStorageNotFoundException("Failed to lookup file by path: " + path));
    if (s3Object.key().endsWith("/")) {
      throw new StudyStorageNotFoundException("Object at path is a folder: " + path);
    }
    return S3Utils.convertS3ObjectToStorageFile(s3Object);
  }

  @Override
  public StorageFolder createFolder(FileStorageLocation location, String path, String name)
      throws StudyStorageException {
    String fullPath = StorageUtils.joinPath(path, name) + "/";
    try {

      // Get the bucket
      String bucketName = (String) location.getIntegrationInstance().findConfigurationValue("bucket")
          .orElseThrow(() -> new StudyStorageException("Missing bucket configuration for location: " + location.getId()));

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
  public StorageFile uploadFile(FileStorageLocation location, String path, File file)
      throws StudyStorageException {

    // Get the bucket
    String bucketName = (String) location.getIntegrationInstance().findConfigurationValue("bucket")
        .orElseThrow(() -> new StudyStorageException("Missing bucket configuration for location: " + location.getId()));

    if (!StoragePermissions.canWrite(location.getPermissions())) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files");
    }

    if (!exists(bucketName, path)) {
      throw new StudyStorageException("Folder not found: " + path);
    }
    String fullPath = StorageUtils.joinPath(path, file.getName());
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fullPath)
          .build();
      client.putObject(request, RequestBody.fromFile(file));
    } catch (Exception e) {
      throw new StudyStorageException("Failed to upload file: " + path, e);
    }
    return findFileByPath(location, fullPath);
  }

  @Override
  public InputStreamResource downloadFile(FileStorageLocation location, String path) throws StudyStorageException {

    // Get the bucket
    String bucketName = (String) location.getIntegrationInstance().findConfigurationValue("bucket")
        .orElseThrow(() -> new StudyStorageException("Missing bucket configuration for location: " + location.getId()));

    if (!exists(bucketName, path)) {
      throw new StudyStorageException("File not found: " + path);
    }

    try {
      return new InputStreamResource(
          client.getObjectAsBytes(b -> b.bucket(bucketName).key(path)).asInputStream());
    } catch (Exception e) {
      throw new StudyStorageException("Failed to download file: " + path, e);
    }
  }

  private boolean exists(String bucketName, String path) {
    LOGGER.debug("Checking if path '{}' exists in bucket: {}", path, bucketName);
    if (path.trim().equals("")) {
      return true;
    }
    HeadObjectRequest request = HeadObjectRequest.builder()
        .bucket(bucketName)
        .key(path)
        .build();
    try {
      HeadObjectResponse response = client.headObject(request);
      LOGGER.debug("Found object: {}", response);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }

}
