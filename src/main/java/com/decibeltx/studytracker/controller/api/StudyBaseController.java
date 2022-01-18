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
import com.decibeltx.studytracker.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.decibeltx.studytracker.mapstruct.dto.StudyDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyFormDto;
import com.decibeltx.studytracker.mapstruct.dto.StudySummaryDto;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
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

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/study")
public class StudyBaseController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyBaseController.class);

  @Autowired(required = false)
  private StudyNotebookService notebookService;

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
      @RequestParam(value = "program", required = false) Long programId
  ) {

    List<Study> studies;

    // Search
    if (!StringUtils.isEmpty(search)) {
      studies = getStudyService().search(search);
    }

    // Find by program
    else if (programId != null) {
      Optional<Program> optional = getProgramService().findById(programId);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find program with ID: " + programId);
      }
      studies = getStudyService().findByProgram(optional.get());
    }

    // Find by owner
    else if (owner != null) {
      Optional<User> optional = getUserService().findById(owner);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + owner);
      }
      studies = getStudyService().findAll()
          .stream()
          .filter(study -> study.getOwner().equals(owner) && study.isActive())
          .collect(Collectors.toList());
    }

    // Find by user
    else if (userId != null) {
      Optional<User> optional = getUserService().findById(userId);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + userId);
      }
      User user = optional.get();
      studies = getStudyService().findByUser(user);
    }

    //
    else if (my) {
      try {
        String username = UserAuthenticationUtils
            .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        User user = getUserService().findByUsername(username)
            .orElseThrow(RecordNotFoundException::new);
        studies = getStudyService().findByUser(user);
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    }

    // Active
    else if (active) {
      studies = getStudyService().findAll()
          .stream()
          .filter(study -> study.isActive() && Arrays.asList(Status.IN_PLANNING, Status.ACTIVE)
              .contains(study.getStatus()))
          .collect(Collectors.toList());
    }

    // Legacy
    else if (legacy) {
      studies = getStudyService().findAll().stream()
          .filter(s -> s.isLegacy() && s.isActive())
          .collect(Collectors.toList());
    } else if (external) {
      studies = getStudyService().findAll().stream()
          .filter(s -> s.getCollaborator() != null)
          .collect(Collectors.toList());
    }

    // Find by code
    else if (code != null) {
      studies = Collections.singletonList(
          getStudyService().findByCode(code).orElseThrow(RecordNotFoundException::new));
    }

    // Find all
    else {
      studies = getStudyService().findAll().stream().filter(Study::isActive)
          .collect(Collectors.toList());
    }

    return this.getStudyMapper().toStudySummaryList(studies);

  }

  @GetMapping("/{id}")
  public StudyDetailsDto getStudy(@PathVariable("id") String studyId) throws RecordNotFoundException {
    return this.getStudyMapper().toStudyDetails(getStudyFromIdentifier(studyId));
  }

  @PostMapping("")
  public HttpEntity<StudyDetailsDto> createStudy(@RequestBody @Valid StudyFormDto dto) {

    LOGGER.info("Creating study");
    LOGGER.info(dto.toString());

    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = UserAuthenticationUtils.getUsernameFromAuthentication(authentication);
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Study study = this.getStudyMapper().fromStudyForm(dto);

    // Study team
    Set<User> team = new HashSet<>();
    for (User u : study.getUsers()) {
      team.add(getUserService().findById(u.getId())
          .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + u.getId())));
    }
    study.setUsers(team);

    // Owner
    study.setOwner(getUserService().findById(study.getOwner().getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + study.getOwner().getId())));

    // If a notebook template was requested, find it
    if (notebookService != null && StringUtils.hasText(dto.getNotebookTemplateId())) {
      Optional<NotebookTemplate> templateOptional =
          notebookService.findEntryTemplateById(dto.getNotebookTemplateId());
      if (templateOptional.isPresent()) {
        getStudyService().create(study, templateOptional.get());
      } else {
        throw new RecordNotFoundException("Could not find notebook entry template: "
            + dto.getNotebookTemplateId());
      }
    } else {
      getStudyService().create(study);
    }

    // Save the record

    Assert.notNull(study.getId(), "Study not persisted.");

    // Publish events
    Activity activity = StudyActivityUtils.fromNewStudy(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(this.getStudyMapper().toStudyDetails(study), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyDetailsDto> updateStudy(@PathVariable("id") String id,
      @RequestBody @Valid StudyFormDto dto) {

    LOGGER.info("Updating study: " + id);
    LOGGER.info(dto.toString());

    // Get current user
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    // Make sure the study exists
    this.getStudyFromIdentifier(id);

    Study study = this.getStudyMapper().fromStudyForm(dto);

    // Study team
    Set<User> team = new HashSet<>();
    for (User u : study.getUsers()) {
      team.add(getUserService().findById(u.getId())
          .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + u.getId())));
    }
    study.setUsers(team);

    // Owner
    study.setOwner(getUserService().findById(study.getOwner().getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find user: " + study.getOwner().getId())));

    getStudyService().update(study);

    // Publish events
    Activity activity = StudyActivityUtils.fromUpdatedStudy(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(this.getStudyMapper().toStudyDetails(study), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteStudy(@PathVariable("id") String id) {

    LOGGER.info("Deleting study: " + id);

    Study study = getStudyFromIdentifier(id);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);

    getStudyService().delete(study);

    // Publish events
    Activity activity = StudyActivityUtils.fromDeletedStudy(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public void updateStudyStatus(@PathVariable("id") String id,
      @RequestBody Map<String, Object> params) throws StudyTrackerException {

    if (!params.containsKey("status")) {
      throw new StudyTrackerException("No status label provided.");
    }

    Study study = getStudyFromIdentifier(id);
    Status oldStatus = study.getStatus();

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);

    String label = (String) params.get("status");
    Status status = Status.valueOf(label);
    LOGGER.info(String.format("Setting status of study %s to %s", id, label));
    getStudyService().updateStatus(study, status);

    // Publish events
    Activity activity = StudyActivityUtils.fromStudyStatusChange(study, user, oldStatus, status);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);
  }

}
