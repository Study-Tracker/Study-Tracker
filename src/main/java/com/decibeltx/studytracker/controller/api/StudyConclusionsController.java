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
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.decibeltx.studytracker.mapstruct.dto.StudyConclusionsDto;
import com.decibeltx.studytracker.mapstruct.mapper.StudyConclusionsMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyConclusions;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.StudyConclusionsService;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/api/study/{studyId}/conclusions")
@RestController
public class StudyConclusionsController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyConclusionsController.class);

  @Autowired
  private StudyConclusionsService studyConclusionsService;

  @Autowired
  private StudyConclusionsMapper conclusionsMapper;

  @GetMapping("")
  public StudyConclusionsDto getStudyConclusions(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    Optional<StudyConclusions> optional = studyConclusionsService.findStudyConclusions(study);
    if (optional.isPresent()) {
      return conclusionsMapper.toDto(optional.get());
    }
    throw new RecordNotFoundException("No conclusions found for study: " + study.getCode());
  }

  @PostMapping("")
  public HttpEntity<StudyConclusionsDto> newStudyConclusions(@PathVariable("studyId") String studyId,
      @RequestBody @Valid StudyConclusionsDto dto) {
    Study study = getStudyFromIdentifier(studyId);
    if (dto.getId() != null || study.getConclusions() != null) {
      throw new StudyTrackerException("Study conclusions object already exists.");
    }
    LOGGER.info(
        String.format("Creating conclusions for study %s: %s", studyId, dto.toString()));

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    StudyConclusions conclusions = conclusionsMapper.fromDto(dto);
    studyConclusionsService.addStudyConclusions(study, conclusions);

    // Publish events
    Activity activity = StudyActivityUtils.fromNewConclusions(study, user, conclusions);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(conclusionsMapper.toDto(conclusions), HttpStatus.CREATED);
  }

  @PutMapping("")
  public HttpEntity<StudyConclusionsDto> editStudyConclusions(@PathVariable("studyId") String studyId,
      @RequestBody @Valid StudyConclusionsDto dto) {
    LOGGER.info(
        String.format("Updating conclusions for study %s: %s", studyId, dto.toString()));
    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    StudyConclusions conclusions = conclusionsMapper.fromDto(dto);
    studyConclusionsService.updateStudyConclusions(study, conclusions);

    Activity activity = StudyActivityUtils.fromUpdatedConclusions(study, user, conclusions);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(conclusionsMapper.toDto(conclusions), HttpStatus.OK);
  }

  @DeleteMapping("")
  public HttpEntity<?> deleteStudyConclusions(@PathVariable("studyId") String studyId) {
    LOGGER.info(String.format("Deleting conclusions for study %s", studyId));
    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    studyConclusionsService.deleteStudyConclusions(study);

    Activity activity = StudyActivityUtils.fromDeletedConclusions(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
