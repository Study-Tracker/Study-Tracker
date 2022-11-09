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
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.service.StorageLocationService;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.DataFileStorageServiceLookup;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StoragePermissions;
import io.studytracker.storage.StorageUtils;
import java.nio.file.Path;
import java.util.List;
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
  private StorageLocationService storageLocationService;

  @Autowired
  private Environment environment;

  @GetMapping("/locations")
  public List<FileStorageLocation> getFileStorageLocations() {
    return storageLocationService.findAll();
  }

  @GetMapping("")
  public StorageFolder getDataStorageFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "locationId") Long locationId
  ) throws FileStorageException {
    LOGGER.debug("Getting data storage folder");
    FileStorageLocation location = storageLocationService.findById(locationId)
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found"));
    if (path == null) path = location.getRootFolderPath();
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));
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
    FileStorageLocation location = storageLocationService.findById(locationId)
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found"));
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
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));;
    StorageFile storageFile = storageService.saveFile(location, path, localPath.toFile());
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
    FileStorageLocation location = storageLocationService.findById(locationId)
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found"));
    if (!StoragePermissions.canWrite(location.getPermissions())) {
      throw new InsufficientPrivilegesException("Insufficient privileges to create folder");
    }
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));;
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
    FileStorageLocation location = storageLocationService.findById(locationId)
        .orElseThrow(() -> new RecordNotFoundException("File storage location not found"));
    DataFileStorageService storageService = dataFileStorageServiceLookup.lookup(location.getType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));;
    ByteArrayResource resource = (ByteArrayResource) storageService.fetchFile(location, path);
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
