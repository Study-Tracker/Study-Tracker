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
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.DataFileStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
  private DataFileStorageService dataFileStorageService;

  @Autowired
  private FileSystemStorageService fileSystemStorageService;

  @Autowired
  private Environment environment;

  @GetMapping("")
  private StorageFolder getDataStorageFolder(
      @RequestParam(name = "path", required = false) String path,
      @RequestParam(name = "folderId", required = false) String folderId
  ) {
    LOGGER.debug("Getting data storage folder");
    String rootPath = environment.getRequiredProperty("egnyte.root-path");
    if (path == null) path = rootPath;
    try {
      StorageFolder folder = dataFileStorageService.findFolderByPath(path);
      return folder;
    } catch (Exception e) {
      throw new RecordNotFoundException("Data storage folder not found: " + path);
    }
  }

  @PostMapping("/upload")
  private HttpEntity<StorageFile> uploadFilesToFolder(
      @RequestParam(name = "path") String path,
      @RequestParam("file") MultipartFile file
  ) throws Exception {
    LOGGER.info("Uploading file {} to data storage folder {}", file.getOriginalFilename(), path);

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
    StorageFile storageFile = dataFileStorageService.uploadFile(path, localPath.toFile());
    LOGGER.debug("Uploaded file: " + storageFile.toString());
    return new ResponseEntity<>(storageFile, HttpStatus.OK);

  }

  @PostMapping("/create-folder")
  private HttpEntity<StorageFolder> createNewFolder(
      @RequestParam(name = "path") String path,
      @RequestParam(name = "folderName") String folderName
  ) throws Exception {
    LOGGER.info("Creating new folder {} in data storage folder {}", folderName, path);
    StorageFolder storageFolder = dataFileStorageService.createFolder(path, folderName);
    LOGGER.debug("Created folder: " + storageFolder.toString());
    return new ResponseEntity<>(storageFolder, HttpStatus.OK);
  }


}
