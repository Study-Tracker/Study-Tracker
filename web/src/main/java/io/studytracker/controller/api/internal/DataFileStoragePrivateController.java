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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.SupportedIntegration;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.DataFileStorageServiceLookup;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StorageUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/internal/data-files")
@RestController
public class DataFileStoragePrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFileStoragePrivateController.class);

  @Autowired
  private DataFileStorageServiceLookup dataFileStorageServiceLookup;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  @Autowired
  private Environment environment;

  private final List<FileStorageLocation> fileStorageLocations = new ArrayList<>();

  // TODO: remove
  @PostConstruct
  public void init() {

    long integrationCount = 0L;
    long instanceCount = 0L;
    long locationCount = 0L;

    if (environment.containsProperty("egnyte.root-path")) {

      integrationCount++;
      SupportedIntegration egnyteIntegration = new SupportedIntegration();
      egnyteIntegration.setName("Egnyte");
      egnyteIntegration.setId(integrationCount);
      egnyteIntegration.setActive(true);
      egnyteIntegration.setVersion(1);

      instanceCount++;
      IntegrationInstance egnyteInstance = new IntegrationInstance();
      egnyteInstance.setId(instanceCount);
      egnyteInstance.setSupportedIntegration(egnyteIntegration);
      egnyteInstance.setDisplayName("Egnyte");
      egnyteInstance.setName(environment.getRequiredProperty("egnyte.root-url"));
      egnyteInstance.setActive(true);

      locationCount++;
      FileStorageLocation egnyteStorageLocation = new FileStorageLocation();
      egnyteStorageLocation.setId(locationCount);
      egnyteStorageLocation.setIntegrationInstance(egnyteInstance);
      egnyteStorageLocation.setType(StorageLocationType.EGNYTE_API);
      egnyteStorageLocation.setRootFolderPath(environment.getRequiredProperty("egnyte.root-path"));
      egnyteStorageLocation.setDisplayName("Egnyte");
      egnyteStorageLocation.setName(environment.getRequiredProperty("egnyte.root-path"));
      egnyteStorageLocation.setPermissions(StoragePermissions.READ_WRITE);
      egnyteStorageLocation.setStudyDefault(true);
      egnyteStorageLocation.setDataDefault(false);

      fileStorageLocations.add(egnyteStorageLocation);
    }

    if (environment.containsProperty("aws.s3-buckets")) {

      integrationCount++;
      SupportedIntegration awsIntegration = new SupportedIntegration();
      awsIntegration.setName("Amazon Web Services");
      awsIntegration.setId(integrationCount);
      awsIntegration.setActive(true);
      awsIntegration.setVersion(1);

      instanceCount++;
      IntegrationInstance awsInstance = new IntegrationInstance();
      awsInstance.setId(instanceCount);
      awsInstance.setSupportedIntegration(awsIntegration);
      awsInstance.setDisplayName("AWS S3");
      awsInstance.setName("AWS S3");
      awsInstance.setActive(true);
      awsInstance.setConfiguration(Collections.singletonMap("buckets", environment.getRequiredProperty("aws.s3-buckets")));

      for (String bucket: environment.getRequiredProperty("aws.s3-buckets").split(",")) {
        locationCount++;
        bucket = bucket.trim();
        FileStorageLocation s3StorageLocation = new FileStorageLocation();
        s3StorageLocation.setId(locationCount);
        s3StorageLocation.setType(StorageLocationType.AWS_S3);
        s3StorageLocation.setIntegrationInstance(awsInstance);
        s3StorageLocation.setRootFolderPath("");
        s3StorageLocation.setDisplayName(bucket);
        s3StorageLocation.setName(bucket);
        s3StorageLocation.setPermissions(StoragePermissions.READ_WRITE);
        s3StorageLocation.setStudyDefault(false);
        s3StorageLocation.setDataDefault(true);

        fileStorageLocations.add(s3StorageLocation);
      }

    }

  }

  @GetMapping("/locations")
  public List<FileStorageLocation> getFileStorageLocations() {
    return fileStorageLocations;
  }

  private FileStorageLocation lookupFileStorageLocation(Long id) {
    return fileStorageLocations.stream()
        .filter(location -> location.getId().equals(id))
        .findFirst()
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found"));
  }

  @GetMapping("")
  public StorageFolder getDataStorageFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "locationId") Long locationId
  ) {
    LOGGER.debug("Getting data storage folder");
    FileStorageLocation location = lookupFileStorageLocation(locationId);
    if (path == null) path = location.getRootFolderPath();
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType());
    try {
      return storageService.findFolderByPath(location, path);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RecordNotFoundException("Data storage folder not found: " + path, e);
    }
  }

  @PostMapping("/upload")
  public HttpEntity<StorageFile> uploadFilesToFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "locationId") Long locationId,
      @RequestParam("file") MultipartFile file
  ) throws Exception {

    LOGGER.info("Uploading file {} to data storage folder {}", file.getOriginalFilename(), path);

    // Get the location and check permissions
    FileStorageLocation location = lookupFileStorageLocation(locationId);
    if (!StoragePermissions.canWrite(location.getPermissions())) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files.");
    }

    // Save the file locally
    Path localPath;
    try {
      localPath = fileSystemStorageService.store(file);
      LOGGER.debug("Local file path: " + localPath.toString());
    } catch (FileStorageException e) {
      e.printStackTrace();
      throw new FileStorageException("Failed to upload file: " + file.getOriginalFilename() ,e);
    }

    // Upload to the cloud service
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType());
    StorageFile storageFile = storageService.uploadFile(location, path, localPath.toFile());
    LOGGER.debug("Uploaded file: " + storageFile.toString());
    return new ResponseEntity<>(storageFile, HttpStatus.OK);

  }

  @PostMapping("/create-folder")
  public HttpEntity<StorageFolder> createNewFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "locationId") Long locationId,
      @RequestParam(name = "folderName") String folderName
  ) throws Exception {
    LOGGER.info("Creating new folder '{}' in data storage folder '{}'", folderName, path);
    FileStorageLocation location = lookupFileStorageLocation(locationId);
    if (!StoragePermissions.canWrite(location.getPermissions())) {
      throw new InsufficientPrivilegesException("Insufficient privileges to create folder");
    }
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType());
    StorageFolder storageFolder = storageService.createFolder(location, path, folderName);
    LOGGER.debug("Created folder: " + storageFolder.toString());
    return new ResponseEntity<>(storageFolder, HttpStatus.OK);
  }

  @GetMapping("/download")
  public HttpEntity<Resource> downloadFile(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "locationId") Long locationId
  ) throws Exception {
    LOGGER.info("Downloading file from data storage folder {}", path);
    FileStorageLocation location = lookupFileStorageLocation(locationId);
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType());
    ByteArrayResource resource = (ByteArrayResource) storageService.downloadFile(location, path);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename(StorageUtils.getFileName(path))
        .build());
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(resource.contentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

}
