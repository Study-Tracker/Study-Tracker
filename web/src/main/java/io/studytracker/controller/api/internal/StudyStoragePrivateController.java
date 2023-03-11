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
import io.studytracker.mapstruct.dto.response.StudyStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.mapper.StorageDriveFolderMapper;
import io.studytracker.model.Study;
import io.studytracker.model.StudyStorageFolder;
import io.studytracker.repository.StudyStorageFolderRepository;
import io.studytracker.service.FileSystemStorageService;
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
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/study/{studyId}/storage")
@RestController
public class StudyStoragePrivateController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyStoragePrivateController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired private StudyStorageFolderRepository studyStorageFolderRepository;

  @Autowired private StorageDriveFolderMapper storageDriveFolderMapper;

  @GetMapping("")
  public List<StudyStorageDriveFolderSummaryDto> getStudyStorageFolders(@PathVariable("studyId") String studyId) {
    LOGGER.info("Fetching storage folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);
    List<StudyStorageFolder> studyStorageFolders = studyStorageFolderRepository
        .findByStudyId(study.getId());
    return storageDriveFolderMapper.toStudyFolderSummaryDto(studyStorageFolders);
  }

//  @GetMapping("/{folderId}/contents")
//  public StorageFolder getStudyStorageFolder(@PathVariable("studyId") String studyId,
//      @PathVariable("folderId") Long folderId)
//      throws Exception {
//    LOGGER.info("Fetching storage folder for study: " + studyId);
//    Study study = getStudyFromIdentifier(studyId);
//
//    FileStorageLocation location = storageLocationService.findByFileStoreFolderId(folderId);
//    StudyStorageService studyStorageService = storageLocationService.lookupStudyStorageService(location);
//    return studyStorageService.findFolder(location, study);
//  }
//
//  @PostMapping("")
//  public HttpEntity<StorageFile> uploadStudyFile(
//      @PathVariable("studyId") String studyId,
//      @RequestParam("file") MultipartFile file
//  ) throws Exception {
//    LOGGER.info("Uploaded file: " + file.getOriginalFilename());
//    Study study = getStudyFromIdentifier(studyId);
//    Path path;
//    try {
//      path = fileStorageService.store(file);
//      LOGGER.info(path.toString());
//    } catch (FileStorageException e) {
//      e.printStackTrace();
//      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//    FileStorageLocation location = storageLocationService.findByFileStoreFolder(study.getPrimaryStorageFolder());
//    StudyStorageService studyStorageService = storageLocationService.lookupStudyStorageService(location);
//    StorageFile storageFile = studyStorageService.saveFile(location, path.toFile(), study);
//
//    // Publish events
//    Activity activity =
//        StudyActivityUtils.fromFileUpload(study, this.getAuthenticatedUser(), storageFile);
//    getActivityService().create(activity);
//    getEventsService().dispatchEvent(activity);
//
//    return new ResponseEntity<>(storageFile, HttpStatus.CREATED);
//  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("studyId") String studyId) {
    LOGGER.info("Repairing storage folder for study: " + studyId);
    Study study = this.getStudyFromIdentifier(studyId);
    getStudyService().repairStorageFolder(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
