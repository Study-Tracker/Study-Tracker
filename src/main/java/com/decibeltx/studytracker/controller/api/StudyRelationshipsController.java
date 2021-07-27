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
import com.decibeltx.studytracker.mapstruct.dto.StudyRelationshipDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.StudyRelationshipSlimDto;
import com.decibeltx.studytracker.mapstruct.mapper.StudyRelationshipMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.RelationshipType;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyRelationship;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.StudyRelationshipService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/study/{id}/relationships")
@RestController
public class StudyRelationshipsController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyRelationshipsController.class);

  @Autowired
  private StudyRelationshipService studyRelationshipService;

  @Autowired
  private StudyRelationshipMapper relationshipMapper;

  @GetMapping("")
  public List<StudyRelationshipDetailsDto> getStudyRelationships(@PathVariable("id") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return relationshipMapper.toDetailsList(studyRelationshipService.findStudyRelationships(study));
  }

  @PostMapping("")
  public HttpEntity<StudyRelationshipDetailsDto> createStudyRelationship(
      @PathVariable("id") String sourceStudyId,
      @RequestBody @Valid StudyRelationshipSlimDto dto) {

    LOGGER
        .info(String.format("Creating new study relationship for study %s: type=%s targetStudy=%s",
            sourceStudyId, dto.getType(), dto.getTargetStudyId()));

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Study sourceStudy = this.getStudyFromIdentifier(sourceStudyId);
    Study targetStudy = this.getStudyFromIdentifier(dto.getTargetStudyId().toString());
    StudyRelationship studyRelationship = studyRelationshipService
        .addStudyRelationship(sourceStudy, targetStudy, dto.getType());

    this.getStudyService().markAsUpdated(sourceStudy, user);
    this.getStudyService().markAsUpdated(targetStudy, user);

    // Publish events
    Activity sourceActivity = StudyActivityUtils
        .fromNewStudyRelationship(sourceStudy, targetStudy, user, studyRelationship);
    this.getActivityService().create(sourceActivity);
    this.getEventsService().dispatchEvent(sourceActivity);

    StudyRelationship inverseRelationship
        = new StudyRelationship(RelationshipType.getInverse(studyRelationship.getType()), targetStudy, sourceStudy);
    Activity targetActivity = StudyActivityUtils
        .fromNewStudyRelationship(targetStudy, sourceStudy, user, inverseRelationship);
    this.getActivityService().create(targetActivity);
    this.getEventsService().dispatchEvent(targetActivity);

    return new ResponseEntity<>(relationshipMapper.toDetails(studyRelationship), HttpStatus.CREATED);

  }

  @DeleteMapping("/{relationshipId}")
  public HttpEntity<?> deleteStudyRelationship(@PathVariable("id") String sourceStudyId,
      @PathVariable("relationshipId") Long relationshipId) {

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Optional<StudyRelationship> optional = studyRelationshipService.findById(relationshipId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find study relationship: " + relationshipId);
    }
    StudyRelationship relationship = optional.get();

    Study sourceStudy = getStudyFromIdentifier(relationship.getSourceStudy().getId().toString());
    Study targetStudy = getStudyFromIdentifier(relationship.getTargetStudy().getId().toString());

    studyRelationshipService.removeStudyRelationship(sourceStudy, targetStudy);
//    this.getStudyService().markAsUpdated(sourceStudy, user);
//    this.getStudyService().markAsUpdated(targetStudy, user);

    // Publish events
    Activity sourceActivity = StudyActivityUtils.fromDeletedStudyRelationship(sourceStudy, user);
    getActivityService().create(sourceActivity);
    getEventsService().dispatchEvent(sourceActivity);
    Activity targetActivity = StudyActivityUtils.fromDeletedStudyRelationship(targetStudy, user);
    getActivityService().create(targetActivity);
    getEventsService().dispatchEvent(targetActivity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
