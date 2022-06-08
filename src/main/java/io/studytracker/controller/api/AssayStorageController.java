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

package io.studytracker.controller.api;

import io.studytracker.controller.UserAuthenticationUtils;
import io.studytracker.events.util.AssayActivityUtils;
import io.studytracker.exception.FileStorageException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.User;
import io.studytracker.service.FileSystemStorageService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
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

@RequestMapping({"/api/assay/{assayId}/storage", "/api/study/{studyId}/assays/{assayId}/storage"})
@RestController
public class AssayStorageController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayStorageController.class);

  @Autowired private FileSystemStorageService fileStorageService;

  @Autowired(required = false)
  private StudyStorageService studyStorageService;

  @GetMapping("")
  public StorageFolder getStorageFolder(@PathVariable("assayId") String assayId) throws Exception {
    LOGGER.info("Fetching storage folder for assay: " + assayId);
    Assay assay = getAssayFromIdentifier(assayId);
    return studyStorageService.getAssayFolder(assay);
  }

  @PostMapping("")
  public HttpEntity<StorageFile> uploadFile(
      @PathVariable("assayId") String assayId, @RequestParam("file") MultipartFile file)
      throws Exception {
    LOGGER.info("Uploaded file: " + file.getOriginalFilename());
    String username =
        UserAuthenticationUtils.getUsernameFromAuthentication(
            SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username).orElseThrow(RecordNotFoundException::new);
    Assay assay = getAssayFromIdentifier(assayId);
    Path path;
    try {
      path = fileStorageService.store(file);
      LOGGER.info(path.toString());
    } catch (FileStorageException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    StorageFile storageFile = studyStorageService.saveAssayFile(path.toFile(), assay);

    // Publish events
    Activity activity = AssayActivityUtils.fromFileUpload(assay, user, storageFile);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(storageFile, HttpStatus.CREATED);
  }

  @PostMapping("/repair")
  public HttpEntity<?> repairStorageFolder(@PathVariable("id") String assayId) {
    LOGGER.info("Repairing storage folder for assay: " + assayId);
    Assay assay = this.getAssayFromIdentifier(assayId);
    getAssayService().repairStorageFolder(assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
