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

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.mapstruct.dto.form.StorageDriveFolderFormDto;
import io.studytracker.mapstruct.dto.response.StudyStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyStorageFolder;
import io.studytracker.repository.StudyStorageFolderRepository;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageDriveFolderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/internal/study/{studyId}/storage")
@RestController
public class StudyStoragePrivateController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyStoragePrivateController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired private StudyStorageFolderRepository studyStorageFolderRepository;

  @Autowired private StorageDriveFolderMapper storageDriveFolderMapper;
  
  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @GetMapping("")
  public List<StudyStorageDriveFolderSummaryDto> getStudyStorageFolders(@PathVariable("studyId") String studyId) {
    LOGGER.info("Fetching storage folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);
    List<StudyStorageFolder> studyStorageFolders = studyStorageFolderRepository
        .findByStudyId(study.getId());
    return storageDriveFolderMapper.toStudyFolderSummaryDto(studyStorageFolders);
  }

  @PatchMapping("")
  public HttpEntity<?> addFolderToStudy(@PathVariable("studyId") String studyId,
          @RequestBody StorageDriveFolderFormDto dto) {
    LOGGER.info("Adding storage folder {} to study {}", dto.getPath(), studyId);
    Study study = getStudyFromIdentifier(studyId);
    StorageDrive drive = storageDriveFolderService.findDriveById(dto.getStorageDriveId())
            .orElseThrow(() -> new IllegalArgumentException("Storage drive not found: " + dto.getStorageDriveId()));
    StorageDriveFolder folder = storageDriveFolderService
            .registerFolder(storageDriveFolderMapper.fromFormDto(dto), drive);
    study.addStorageFolder(folder);
    this.getStudyService().update(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("studyId") String studyId) {
    LOGGER.info("Repairing storage folder for study: " + studyId);
    Study study = this.getStudyFromIdentifier(studyId);
    getStudyService().repairStorageFolder(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
