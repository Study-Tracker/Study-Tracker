/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.exception.FileStorageException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.FileSystemStorageService;
import com.decibeltx.studytracker.storage.StorageFile;
import com.decibeltx.studytracker.storage.StorageFolder;
import com.decibeltx.studytracker.storage.StudyStorageService;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/api/study/{studyId}/storage")
@RestController
public class StudyStorageController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyStorageController.class);

  @Autowired
  private FileSystemStorageService fileStorageService;

  @Autowired(required = false)
  private StudyStorageService studyStorageService;

  @GetMapping("")
  public StorageFolder getStudyStorageFolder(@PathVariable("studyId") String studyId)
      throws Exception {
    LOGGER.info("Fetching storage folder for study: " + studyId);
    Study study = getStudyFromIdentifier(studyId);
    return studyStorageService.getStudyFolder(study);
  }

  @PostMapping("")
  public HttpEntity<StorageFile> uploadStudyFile(@PathVariable("studyId") String studyId,
      @RequestParam("file") MultipartFile file) throws Exception {
    LOGGER.info("Uploaded file: " + file.getOriginalFilename());
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    Study study = getStudyFromIdentifier(studyId);
    Path path;
    try {
      path = fileStorageService.store(file);
      LOGGER.info(path.toString());
    } catch (FileStorageException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    StorageFile storageFile = studyStorageService.saveStudyFile(path.toFile(), study);

    // Publish events
    Activity activity = StudyActivityUtils
        .fromFileUpload(study, user, storageFile);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(storageFile, HttpStatus.CREATED);
  }

}
