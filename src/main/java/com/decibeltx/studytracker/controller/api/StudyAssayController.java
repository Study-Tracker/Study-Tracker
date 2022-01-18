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
import com.decibeltx.studytracker.eln.NotebookTemplate;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.exception.NotebookException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.decibeltx.studytracker.mapstruct.dto.AssayDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.AssayFormDto;
import com.decibeltx.studytracker.mapstruct.dto.AssaySummaryDto;
import com.decibeltx.studytracker.mapstruct.mapper.AssayMapper;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
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

  @Autowired
  private AssayMapper assayMapper;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @GetMapping("")
  public List<AssaySummaryDto> findStudyAssays(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return assayMapper.toAssaySummaryList(this.getAssayService().findByStudyId(study.getId()));
  }

  @GetMapping("/{assayId}")
  public AssayDetailsDto findById(@PathVariable("assayId") String assayId) throws RecordNotFoundException {
    return assayMapper.toAssayDetails(getAssayFromIdentifier(assayId));
  }

  @PostMapping("")
  public HttpEntity<AssayDetailsDto> create(@PathVariable("studyId") String studyId,
      @RequestBody @Valid AssayFormDto dto)
          throws RecordNotFoundException, NotebookException {

    LOGGER.info("Creating assay");
    LOGGER.info(dto.toString());

    Study study = this.getStudyFromIdentifier(studyId);

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Assay assay = assayMapper.fromAssayForm(dto);
    Assay created;

    // If a notebook template was requested, find it
    if (notebookService != null && StringUtils.hasText(dto.getNotebookTemplateId())) {
      Optional<NotebookTemplate> templateOptional =
          notebookService.findEntryTemplateById(dto.getNotebookTemplateId());
      if (templateOptional.isPresent()) {
        created = this.createAssay(assay, study, user, templateOptional.get());
      } else {
        throw new RecordNotFoundException("Could not find notebook entry template: "
            + dto.getNotebookTemplateId());
      }
    } else {
      created = this.createAssay(assay, study, user);
    }

    return new ResponseEntity<>(assayMapper.toAssayDetails(created), HttpStatus.CREATED);

  }

  @PutMapping("/{assayId}")
  public HttpEntity<AssayDetailsDto> update(@PathVariable("assayId") String assayId,
      @RequestBody @Valid AssayFormDto dto) {
    LOGGER.info("Updating assay");
    LOGGER.info(dto.toString());
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    Assay assay = assayMapper.fromAssayForm(dto);
    this.updateAssay(assay, user);
    return new ResponseEntity<>(assayMapper.toAssayDetails(assay), HttpStatus.OK);
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

    Assay assay = this.getAssayFromIdentifier(id);

    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = UserAuthenticationUtils.getUsernameFromAuthentication(authentication);
    User user = this.getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of assay %s to %s", id, label));

    this.updateAssayStatus(assay.getId(), status, user);

    return new ResponseEntity<>(HttpStatus.OK);

  }

}
