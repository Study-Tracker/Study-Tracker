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

import com.decibeltx.studytracker.core.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StudyBaseController extends StudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyBaseController.class);

  @GetMapping("")
  public List<Study> getAllStudies(
      @RequestParam(value = "code", required = false) String code,
      @RequestParam(value = "owner", required = false) String owner,
      @RequestParam(value = "user", required = false) String userId,
      @RequestParam(value = "active", defaultValue = "false") boolean active,
      @RequestParam(value = "legacy", defaultValue = "false") boolean legacy,
      @RequestParam(value = "external", defaultValue = "false") boolean external,
      @RequestParam(value = "my", defaultValue = "false") boolean my,
      @RequestParam(value = "search", required = false) String search,
      @RequestParam(value = "program", required = false) String programId
  ) {

    // Search
    if (!StringUtils.isEmpty(search)) {
      return getStudyService().search(search);
    }

    // Find by program
    else if (programId != null) {
      Optional<Program> optional = getProgramService().findById(programId);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find program with ID: " + programId);
      }
      return getStudyService().findByProgram(optional.get());
    }

    // Find by owner
    else if (owner != null) {
      Optional<User> optional = getUserService().findById(owner);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + owner);
      }
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.getOwner().equals(owner) && study.isActive())
          .collect(Collectors.toList());
    }

    // Find by user TODO
    else if (userId != null) {
      Optional<User> optional = getUserService().findById(userId);
      if (!optional.isPresent()) {
        throw new RecordNotFoundException("Cannot find user record: " + userId);
      }
      User user = optional.get();
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.getOwner().getId().equals(user.getId()) && study.isActive())
          .collect(Collectors.toList());
    }

    // My studies TODO
    else if (my) {
      try {
        String username = UserAuthenticationUtils
            .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
        User user = getUserService().findByUsername(username)
            .orElseThrow(RecordNotFoundException::new);
        return getStudyService().findAll().stream()
            .filter(s -> s.getOwner().equals(user))
            .collect(Collectors.toList());
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    }

    // Active
    else if (active) {
      return getStudyService().findAll()
          .stream()
          .filter(study -> study.isActive() && Arrays.asList(Status.IN_PLANNING, Status.ACTIVE)
              .contains(study.getStatus()))
          .collect(Collectors.toList());
    }

    // Legacy
    else if (legacy) {
      return getStudyService().findAll().stream()
          .filter(s -> s.isLegacy() && s.isActive())
          .collect(Collectors.toList());
    } else if (external) {
      return getStudyService().findAll().stream()
          .filter(s -> s.getCollaborator() != null)
          .collect(Collectors.toList());
    }

    // Find by code
    else if (code != null) {
      return Collections.singletonList(
          getStudyService().findByCode(code).orElseThrow(RecordNotFoundException::new));
    }

    // Find all
    else {
      return getStudyService().findAll().stream().filter(Study::isActive)
          .collect(Collectors.toList());
    }
  }

  @GetMapping("/{id}")
  public Study getStudy(@PathVariable("id") String studyId) throws RecordNotFoundException {
    return getStudyFromIdentifier(studyId);
  }

  @PostMapping("")
  public HttpEntity<Study> createStudy(@RequestBody Study study) {

    LOGGER.info("Creating study");
    LOGGER.info(study.toString());

    // Get authenticated user
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = UserAuthenticationUtils.getUsernameFromAuthentication(authentication);

    // Created by
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    study.setCreatedBy(user);

    // Study team
    List<User> team = new ArrayList<>();
    for (User u : study.getUsers()) {
      team.add(getUserService().findByUsername(u.getUsername())
          .orElseThrow(RecordNotFoundException::new));
    }
    study.setUsers(team);

    // Owner
    study.setOwner(getUserService().findByUsername(study.getOwner().getUsername())
        .orElseThrow(RecordNotFoundException::new));

    // Save the record
    getStudyService().create(study);
    Assert.notNull(study.getId(), "Study not persisted.");

    // Publish events
    Activity activity = StudyActivityUtils.fromNewStudy(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(study, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<Study> updateStudy(@PathVariable("id") String id, @RequestBody Study study) {
    LOGGER.info("Updating study");
    LOGGER.info(study.toString());

    // Last modified by
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);
    study.setLastModifiedBy(user);

    // Study team
    List<User> team = new ArrayList<>();
    for (User u : study.getUsers()) {
      team.add(getUserService().findByUsername(u.getUsername())
          .orElseThrow(RecordNotFoundException::new));
    }
    study.setUsers(team);

    // Owner
    study.setOwner(getUserService().findByUsername(study.getOwner().getUsername())
        .orElseThrow(RecordNotFoundException::new));

    getStudyService().update(study);

    // Publish events
    Activity activity = StudyActivityUtils.fromUpdatedStudy(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(study, HttpStatus.CREATED);
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
