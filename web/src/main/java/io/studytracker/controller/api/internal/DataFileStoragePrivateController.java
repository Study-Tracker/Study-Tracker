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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.mapstruct.mapper.StorageDriveMapper;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/internal/data-files")
@RestController
public class DataFileStoragePrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataFileStoragePrivateController.class);

  @Autowired
  private StudyStorageServiceLookup studyStorageServiceLookup;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private StorageDriveMapper driveMapper;

  @Autowired
  private StorageDriveFolderMapper folderMapper;

  @RequestMapping(value ="", method = RequestMethod.HEAD)
  public HttpEntity<?> checkFolder(
      @RequestParam(name = "path") String rawPath,
      @RequestParam(name = "folderId") Long folderId
  ) throws FileStorageException {
    LOGGER.debug("Checking for storage folder: {}: {}", folderId, rawPath);
    StorageDriveFolder folder = storageDriveFolderService.findById(folderId)
        .orElseThrow(() -> new RecordNotFoundException("Data storage folder not found"));
    StudyStorageService storageService = studyStorageServiceLookup.lookup(folder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));
    String path = rawPath != null ? rawPath : folder.getPath();
    try {
      boolean exists = storageService.folderExists(folder, path);
      if (exists) {
        return new ResponseEntity<>(HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("")
  public StorageFolder getDataStorageFolder(
      @RequestParam(name = "path") String rawPath,
      @RequestParam(name = "folderId") Long folderId
  ) throws FileStorageException {
    LOGGER.debug("Getting data storage folder: {}: {}", folderId, rawPath);
    StorageDriveFolder folder = storageDriveFolderService.findById(folderId)
        .orElseThrow(() -> new RecordNotFoundException("Data storage folder not found"));
    StudyStorageService storageService = studyStorageServiceLookup.lookup(folder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));
    String path = rawPath != null ? rawPath : folder.getPath();
    try {
      return storageService.findFolderByPath(folder, path);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RecordNotFoundException("Data storage folder not found: " + path, e);
    }
  }

  @PostMapping("/upload")
  public HttpEntity<StorageFile> uploadFilesToFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "folderId") Long folderId,
      @RequestParam("file") MultipartFile file
  ) throws Exception {

    LOGGER.info("Uploading file {} to data storage folder {}", file.getOriginalFilename(), path);
    StorageDriveFolder folder = storageDriveFolderService.findById(folderId)
        .orElseThrow(() -> new RecordNotFoundException("Data storage folder not found"));
    StudyStorageService storageService = studyStorageServiceLookup.lookup(folder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));

    // CHeck the permissions
    if (!folder.isWriteEnabled()) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files to this folder");
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
    File localFile = localPath.toFile();
    StorageFile storageFile = storageService.saveFile(folder, path, localFile);

    try {
      if (localFile.exists()) {
        FileUtils.forceDelete(localFile);
      }
    } catch (IOException e) {
      LOGGER.warn("Unable to delete local file copy: " + file.getName());
      e.printStackTrace();
    }

    LOGGER.debug("Uploaded file: " + storageFile.toString());
    return new ResponseEntity<>(storageFile, HttpStatus.OK);

  }

  @PostMapping("/temp-upload")
  public HttpEntity<Map<String, Object>> uploadTemporaryFile(
      @RequestParam("file") MultipartFile file
  ) throws Exception {

    LOGGER.info("Uploading temp file {}", file.getOriginalFilename());

    // Save the file locally
    Path localPath;
    try {
      localPath = fileSystemStorageService.store(file);
      LOGGER.debug("Local file path: " + localPath);
    } catch (FileStorageException e) {
      e.printStackTrace();
      throw new FileStorageException("Failed to upload file: " + file.getOriginalFilename() ,e);
    }

    return ResponseEntity.ok(Collections.singletonMap("filePath", localPath.toString()));

  }

  @PostMapping("/create-folder")
  public HttpEntity<StorageFolder> createNewFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "folderId") Long parentFolderId,
      @RequestParam(name = "folderName") String folderName
  ) throws Exception {
    LOGGER.info("Creating new folder '{}' in data storage folder '{}'", folderName, path);
    StorageDriveFolder parentFolder = storageDriveFolderService.findById(parentFolderId)
        .orElseThrow(() -> new RecordNotFoundException("Data storage folder not found"));
    StudyStorageService storageService = studyStorageServiceLookup.lookup(parentFolder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));

    if (!parentFolder.isWriteEnabled()) {
      throw new InsufficientPrivilegesException("Insufficient privileges to upload files to this folder");
    }

    StorageFolder storageFolder = storageService.createFolder(parentFolder.getStorageDrive(), path, folderName);
    LOGGER.debug("Created folder: " + storageFolder.toString());
    return new ResponseEntity<>(storageFolder, HttpStatus.OK);
  }

  @GetMapping("/download")
  public HttpEntity<Resource> downloadFile(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "folderId") Long folderId
  ) throws Exception {
    LOGGER.info("Downloading file from data storage folder {}", path);
    StorageDriveFolder folder = storageDriveFolderService.findById(folderId)
        .orElseThrow(() -> new RecordNotFoundException("Data storage folder not found"));
    StudyStorageService storageService = studyStorageServiceLookup.lookup(folder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new FileStorageException("File storage service not found"));

    ByteArrayResource resource = (ByteArrayResource) storageService.fetchFile(folder, path);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentDisposition(ContentDisposition.builder("attachment")
        .filename(FilenameUtils.getName(path))
        .build());
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(resource.contentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

}
