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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StorageDriveFolderDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage-folder")
public class StorageDriveFolderPublicController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDriveFolderPublicController.class);

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  @Autowired
  private FileSystemStorageService fileStorageService;

  @Autowired
  private StorageDriveFolderMapper mapper;

  @GetMapping("")
  public Page<StorageDriveFolderDto> findAllFolders(Pageable pageable) {
    LOGGER.debug("Fethching all storage folders");
    Page<StorageDriveFolder> page = storageDriveFolderService.findAll(pageable);
    return new PageImpl<>(mapper.toDto(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public StorageDriveFolderDto findById(@PathVariable Long id) {
    LOGGER.debug("Fetching storage folder with id {}", id);
    StorageDriveFolder folder = storageDriveFolderService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
    return mapper.toDto(folder);
  }

  @PostMapping("/{id}/upload")
  public HttpEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
    LOGGER.info("Uploading file: " + file.getOriginalFilename());
    StorageDriveFolder folder = storageDriveFolderService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot file folder with ID: " + id));
    StudyStorageService studyStorageService = storageDriveFolderService.lookupStudyStorageService(folder);

    // Save the file locally
    Path path;
    try {
      path = fileStorageService.store(file);
      LOGGER.info(path.toString());
    } catch (FileStorageException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Copy it to the target folder
    File localFile = path.toFile();
    try {
      studyStorageService.saveFile(folder, folder.getPath(), localFile);
    } catch (StudyStorageException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Delete the local copy
    if (localFile.exists()) {
      try {
        FileUtils.forceDelete(localFile);
      } catch (IOException e) {
        LOGGER.warn("Failed to delete local file: " + localFile.getAbsolutePath());
        e.printStackTrace();
      }
    }

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
