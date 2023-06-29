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

import io.studytracker.controller.api.AbstractAssayController;
import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.mapstruct.dto.response.AssayStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayStorageFolder;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.AssayStorageFolderRepository;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping({"/api/internal/assay/{assayId}/storage", "/api/internal/study/{studyId}/assays/{assayId}/storage"})
@RestController
public class AssayStoragePrivateController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayStoragePrivateController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired private AssayStorageFolderRepository folderRepository;

  @Autowired private StorageDriveFolderMapper mapper;
  
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @GetMapping("")
  public List<AssayStorageDriveFolderSummaryDto> getAssayStorageFolders(@PathVariable("assayId") String assayId) {
    LOGGER.info("Fetching storage folder for assay: " + assayId);
    Assay assay = getAssayFromIdentifier(assayId);
    List<AssayStorageFolder> assayStorageFolders = folderRepository.findByAssayId(assay.getId());
    return mapper.toAssayFolderSummaryDto(assayStorageFolders);
  }
  
  @PatchMapping("")
  public HttpEntity<?> addFolderToAssay(@PathVariable("assayId") String assayId,
          @RequestBody StorageDriveFolderFormDto dto) {
    LOGGER.info("Adding storage folder {} to assay {}", dto.getPath(), assayId);
    Assay assay = getAssayFromIdentifier(assayId);
    StorageDrive drive = storageDriveFolderService.findDriveById(dto.getStorageDriveId())
            .orElseThrow(() -> new IllegalArgumentException("Storage drive not found: " + dto.getStorageDriveId()));
    StorageDriveFolder folder = storageDriveFolderService
            .registerFolder(mapper.fromFormDto(dto), drive);
    this.getAssayService().addStorageFolder(assay, folder);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("assayId") String assayId) {
    LOGGER.info("Repairing storage folder for assay: " + assayId);
    Assay assay = this.getAssayFromIdentifier(assayId);
    getAssayService().repairStorageFolder(assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
