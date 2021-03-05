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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.eln.StudyNotebookService;
import com.decibeltx.studytracker.core.exception.NotebookException;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/study/{studyId}/assays")
@RestController
public class StudyAssayController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyAssayController.class);

  @Autowired(required = false)
  private StudyNotebookService studyNotebookService;

  @GetMapping("")
  public List<Assay> findStudyAssays(@PathVariable("studyId") String studyId) {
    return getStudyFromIdentifier(studyId).getAssays().stream()
        .filter(Assay::isActive)
        .collect(Collectors.toList());
  }

  @GetMapping("/{assayId}")
  public Assay findById(@PathVariable("assayId") String assayId) throws RecordNotFoundException {
    return getAssayFromIdentifier(assayId);
  }

  @PostMapping("")
  public HttpEntity<Assay> create(@PathVariable("studyId") String studyId,
      @RequestBody Assay assay)
          throws RecordNotFoundException, NotebookException {
    LOGGER.info("Creating assay");
    LOGGER.info(assay.toString());
    Study study = this.getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    Assay created = this.createAssay(assay, study, user);

    if(!assay.getEntryTemplateId().isEmpty()) {
      if (studyNotebookService == null) {
        throw new RecordNotFoundException("Could not create new entry");
      }
      studyNotebookService.createAssayNotebookEntry(created, assay.getEntryTemplateId());
    }

    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @PutMapping("/{assayId}")
  public HttpEntity<Assay> update(@PathVariable("assayId") String assayId,
      @RequestBody Assay assay) {
    LOGGER.info("Updating assay");
    LOGGER.info(assay.toString());
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    this.updateAssay(assay, user);
    return new ResponseEntity<>(assay, HttpStatus.CREATED);
  }

  @DeleteMapping("/{assayId}")
  public HttpEntity<?> delete(@PathVariable("assayId") String id) {
    LOGGER.info("Deleting assay: " + id);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    this.deleteAssay(id, user);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateStatus(@PathVariable("id") String id,
      @RequestBody Map<String, Object> params) throws StudyTrackerException {

    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }

    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = UserAuthenticationUtils.getUsernameFromAuthentication(authentication);
    User user = this.getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of assay %s to %s", id, label));

    this.updateAssayStatus(id, status, user);

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
