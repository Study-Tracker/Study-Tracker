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
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.mapstruct.dto.form.StudyFormDto;
import io.studytracker.mapstruct.dto.response.StudyDetailsDto;
import io.studytracker.mapstruct.dto.response.StudySummaryDto;
import io.studytracker.model.Activity;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/study")
public class StudyBasePrivateController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyBasePrivateController.class);

  @GetMapping("")
  public List<StudySummaryDto> getAllStudies(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "owner", required = false) Long owner,
      @RequestParam(value = "user", required = false) Long userId,
      @RequestParam(value = "active", defaultValue = "false") boolean active,
      @RequestParam(value = "legacy", defaultValue = "false") boolean legacy,
      @RequestParam(value = "external", defaultValue = "false") boolean external,
      @RequestParam(value = "my", defaultValue = "false") boolean my,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "program", required = false) Long programId) {

    List<Study> studies;

    // Search
    if (StringUtils.hasText(search)) {
      studies = getStudyService().search(search);
    }

    // Find by program
    else if (programId != null) {
      Optional<Program> optional = getProgramService().findById(programId);
      if (optional.isEmpty()) {
        throw new RecordNotFoundException("Cannot find program with ID: " + programId);
      }
      studies = getStudyService().findByProgram(optional.get());
    }

    // Find by owner
    else if (owner != null) {
      Optional<User> optional = getUserService().findById(owner);
      if (optional.isEmpty()) {
        throw new RecordNotFoundException("Cannot find user record: " + owner);
      }
      studies =
          getStudyService().findAll().stream()
              .filter(study -> study.getOwner().getId().equals(owner) && study.isActive())
              .collect(Collectors.toList());
    }

    // Find by user
    else if (userId != null) {
      Optional<User> optional = getUserService().findById(userId);
      if (optional.isEmpty()) {
        throw new RecordNotFoundException("Cannot find user record: " + userId);
      }
      User user = optional.get();
      studies = getStudyService().findByUser(user);
    }

    // Owned by user
    else if (my) {
      try {
        studies = getStudyService().findByUser(this.getAuthenticatedUser());
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    }

    // Active
    else if (active) {
      studies =
          getStudyService().findAll().stream()
              .filter(
                  study ->
                      study.isActive()
                          && Arrays.asList(Status.IN_PLANNING, Status.ACTIVE)
                              .contains(study.getStatus()))
              .collect(Collectors.toList());
    }

    // Legacy
    else if (legacy) {
      studies =
          getStudyService().findAll().stream()
              .filter(s -> s.isLegacy() && s.isActive())
              .collect(Collectors.toList());
    }

    // External
    else if (external) {
      studies =
          getStudyService().findAll().stream()
              .filter(s -> s.getCollaborator() != null)
              .collect(Collectors.toList());
    }

    // Find by code
    else if (code != null) {
      studies =
          Collections.singletonList(
              getStudyService().findByCode(code).orElseThrow(RecordNotFoundException::new));
    }

    // Find all
    else {
      studies = getStudyService().findAll();
    }

    return this.getStudyMapper().toStudySummaryList(studies);
  }

  @GetMapping("/{id}")
  public StudyDetailsDto getStudy(@PathVariable("id") String studyId)
      throws RecordNotFoundException {
    return this.getStudyMapper().toStudyDetails(getStudyFromIdentifier(studyId));
  }

  private void mapPayloadFields(Study study, StudyFormDto dto) {
    Set<User> team = new HashSet<>();
    for (User u : study.getUsers()) {
      team.add(
          this.getUserService().findById(u.getId())
              .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + u.getId())));
    }
    study.setUsers(team);

    // Owner
    study.setOwner(
        this.getUserService()
            .findById(study.getOwner().getId())
            .orElseThrow(
                () ->
                    new RecordNotFoundException("Cannot find user: " + dto.getOwner().getId())));

  }

  @PostMapping("")
  public HttpEntity<StudyDetailsDto> createStudy(@RequestBody @Valid StudyFormDto dto) {
    LOGGER.info("Creating study: {}", dto);
    Study study = this.getStudyMapper().fromStudyForm(dto);
//    StudyOptions options = this.getStudyMapper().optionsFromStudyForm(dto);
    mapPayloadFields(study, dto);
    study = this.createNewStudy(study);
    return new ResponseEntity<>(this.getStudyMapper().toStudyDetails(study), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyDetailsDto> updateStudy(
      @PathVariable("id") String id,
      @RequestBody @Valid StudyFormDto dto
  ) {

    LOGGER.info("Updating study {}: {}", id, dto);

    // Make sure the study exists
    this.getStudyFromIdentifier(id);

    Study study = this.getStudyMapper().fromStudyForm(dto);
    mapPayloadFields(study, dto);
    this.updateExistingStudy(study);

    return new ResponseEntity<>(this.getStudyMapper().toStudyDetails(study), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteStudy(@PathVariable("id") String id) {
    LOGGER.info("Deleting study: " + id);
    Study study = getStudyFromIdentifier(id);
    this.deleteExistingStudy(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateStudyStatus(
      @PathVariable("id") String id,
      @RequestBody Map<String, Object> params
  ) throws StudyTrackerException {
    LOGGER.info("Updating study status: {}", params);
    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }
    Study study = getStudyFromIdentifier(id);
    this.updateExistingStudyStatus(study, params.get("status").toString());
    return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @PutMapping("/{id}/program")
  public StudyDetailsDto updateStudyProgram(
          @PathVariable("id") String id,
          @RequestBody Map<String, Object> params
  ) throws StudyTrackerException {
    LOGGER.info("Updating study program: {}", params);
    
    // Get the study and the new program
    if (!params.containsKey("programId")) {
      throw new StudyTrackerException("No program ID provided.");
    }
    Long programId = Long.parseLong(params.get("programId").toString());
    Study study = getStudyFromIdentifier(id);
    Program oldProgram = study.getProgram();
    Program program = this.getProgramService().findById(programId)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find program with ID: " + programId));
    
    // Move the study
    this.getStudyService().moveStudyToProgram(study, program);
    Study updated = getStudyFromIdentifier(id);
    
    // If the study has assays, move them, too
    for (Assay assay: this.getAssayService().findByStudyId(updated.getId())) {
      this.getAssayService().moveAssayToStudy(assay, updated);
    }
    
    // Publish the activity
    Activity activity = StudyActivityUtils.fromMovedStudy(study, oldProgram, program, this.getAuthenticatedUser());
    this.logActivity(activity);
    return this.getStudyMapper().toStudyDetails(updated);
  }

  @PostMapping("/{id}/restore")
  public HttpEntity<?> restoreRemovedStudy(@PathVariable("id") String id) {
    LOGGER.info("Restoring study: {}", id);
    Study study = getStudyFromIdentifier(id);
    this.restoreRemovedStudy(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
