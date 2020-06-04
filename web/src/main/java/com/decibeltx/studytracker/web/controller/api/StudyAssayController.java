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

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class StudyAssayController extends StudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyAssayController.class);

  @GetMapping("")
  public List<Assay> getStudyAssays(@PathVariable("studyId") String studyId) {
    return getStudyFromIdentifier(studyId).getAssays();
  }

  @GetMapping("/{assayId}")
  public Assay getAssay(@PathVariable("assayId") String assayId) throws RecordNotFoundException {
    return getAssayFromIdentifier(assayId);
  }

  @PostMapping("")
  public HttpEntity<Assay> createAssay(@PathVariable("studyId") String studyId,
      @RequestBody Assay assay) {
    LOGGER.info("Creating assay");
    LOGGER.info(assay.toString());
    Study study = getStudyService().findByCode(studyId).orElseThrow(RecordNotFoundException::new);
    assay.setStudy(study);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setCreatedBy(user);
    getAssayService().create(assay);
    study.getAssays().add(assay);
    getStudyService().update(study);
    return new ResponseEntity<>(assay, HttpStatus.CREATED);
  }

  @PutMapping("/{assayId}")
  public HttpEntity<Assay> updateAssay(@PathVariable("assayId") String assayId,
      @RequestBody Assay assay) {
    LOGGER.info("Updating assay");
    LOGGER.info(assay.toString());
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);
    getAssayService().update(assay);
    return new ResponseEntity<>(assay, HttpStatus.CREATED);
  }

  @DeleteMapping("/{assayId}")
  public HttpEntity<?> deleteAssay(@PathVariable("assayId") String id) {
    LOGGER.info("Deleting assay: " + id);
    Assay assay = getAssayService().findByCode(id).orElseThrow(RecordNotFoundException::new);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByAccountName(username)
        .orElseThrow(RecordNotFoundException::new);
    assay.setLastModifiedBy(user);
    getAssayService().delete(assay);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
