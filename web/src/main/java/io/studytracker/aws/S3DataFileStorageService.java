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

import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3DataFileStorageService  implements DataFileStorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3DataFileStorageService.class);

  @Value("${aws.example-s3-bucket}")
  private String bucketName;

  private final S3Client client;

  public S3DataFileStorageService(S3Client client) {
    this.client = client;
  }

  @Override
  public StorageFolder findFolderByPath(String path) throws StudyStorageNotFoundException {
    LOGGER.debug("Looking up folder by path: {}", path);
    if (!path.endsWith("/")) {
      path += "/";
    }
    try {
      ListObjectsV2Request request = ListObjectsV2Request.builder()
          .bucket(bucketName)
          .prefix(path)
          .delimiter("/")
          .build();
      ListObjectsV2Response response = client.listObjectsV2(request);
      return S3Utils.convertS3ObjectsToStorageFolderWithContents(path, response.contents());
    } catch (Exception e) {
      throw new StudyStorageNotFoundException("Failed to lookup folder by path: " + path, e);
    }
  }

  @Override
  public StorageFile findFileByPath(String path) throws StudyStorageNotFoundException {
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
  public StorageFolder createFolder(String path, String name) throws StudyStorageException {
    String fullPath = StorageUtils.joinPath(path, name) + "/";
    try {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(fullPath)
          .build();
       client.putObject(request, RequestBody.empty());
     } catch (Exception e) {
      throw new StudyStorageException("Failed to create folder: " + path, e);
    }
    return findFolderByPath(fullPath);
  }

  @Override
  public StorageFile uploadFile(String path, File file) throws StudyStorageException {
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
    return findFileByPath(fullPath);
  }
}
