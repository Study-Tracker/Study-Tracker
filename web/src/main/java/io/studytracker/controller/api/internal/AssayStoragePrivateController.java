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

import io.studytracker.controller.api.AbstractAssayController;
import io.studytracker.events.util.AssayActivityUtils;
import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.mapstruct.mapper.FileStoreFolderMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.repository.FileStoreFolderRepository;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.service.StorageLocationService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping({"/api/internal/assay/{assayId}/storage", "/api/internal/study/{studyId}/assays/{assayId}/storage"})
@RestController
public class AssayStoragePrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayStoragePrivateController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired private StorageLocationService storageLocationService;

  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;

  @Autowired private FileStoreFolderMapper fileStoreFolderMapper;

  @GetMapping("")
  public List<FileStoreFolderDetailsDto> getStudyStorageFolders(@PathVariable("assayId") String assayId) {
    LOGGER.info("Fetching storage folder for assay: " + assayId);
    Assay assay = getAssayFromIdentifier(assayId);
    List<FileStoreFolderDetailsDto> folders = new ArrayList<>();
    for (FileStoreFolder fsf: assay.getStorageFolders()) {
      FileStoreFolder folder = fileStoreFolderRepository.findById(fsf.getId())
          .orElseThrow(() -> new RecordNotFoundException("Folder not found: " + fsf.getId()));
      FileStoreFolderDetailsDto dto = fileStoreFolderMapper.toDetailsDto(folder);
      if (assay.getPrimaryStorageFolder().getId().equals(dto.getId())) {
        dto.setPrimary(true);
      }
      folders.add(dto);
    }
    return folders;
  }

  @GetMapping("/{folderId}/contents")
  public StorageFolder getStorageFolderContents(@PathVariable("assayId") String assayId) throws Exception {
    LOGGER.info("Fetching storage folder for assay: " + assayId);
    Assay assay = getAssayFromIdentifier(assayId);
    FileStorageLocation location = storageLocationService.findByFileStoreFolder(assay.getPrimaryStorageFolder());
    StudyStorageService studyStorageService = storageLocationService.lookupStudyStorageService(location);
    return studyStorageService.findFolder(location, assay);
  }

  @PostMapping("")
  public HttpEntity<StorageFile> uploadFile(
      @PathVariable("assayId") String assayId,
      @RequestParam("file") MultipartFile file
  ) throws Exception {
    LOGGER.info("Uploaded file: " + file.getOriginalFilename());
    Assay assay = getAssayFromIdentifier(assayId);
    Path path;
    try {
      path = fileStorageService.store(file);
      LOGGER.info(path.toString());
    } catch (FileStorageException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    FileStorageLocation location = storageLocationService.findByFileStoreFolder(assay.getPrimaryStorageFolder());
    StudyStorageService studyStorageService = storageLocationService.lookupStudyStorageService(location);
    StorageFile storageFile = studyStorageService.saveFile(location, path.toFile(), assay);

    // Publish events
    Activity activity =
        AssayActivityUtils.fromFileUpload(assay, this.getAuthenticatedUser(), storageFile);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(storageFile, HttpStatus.CREATED);
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("assayId") String assayId) {
    LOGGER.info("Repairing storage folder for assay: " + assayId);
    Assay assay = this.getAssayFromIdentifier(assayId);
    getAssayService().repairStorageFolder(assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
