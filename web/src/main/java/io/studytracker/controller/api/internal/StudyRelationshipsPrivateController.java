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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractStudyRelationshipController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.StudyRelationshipFormDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipDetailsDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyRelationship;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/study/{id}/relationships")
@RestController
public class StudyRelationshipsPrivateController extends AbstractStudyRelationshipController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyRelationshipsPrivateController.class);

  @GetMapping("")
  public List<StudyRelationshipDetailsDto> getStudyRelationships(
      @PathVariable("id") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return this.getStudyRelationshipMapper().toDetailsList(
        this.getStudyRelationshipService().findStudyRelationships(study));
  }

  @PostMapping("")
  public HttpEntity<StudyRelationshipDetailsDto> createStudyRelationship(
      @PathVariable("id") String sourceStudyId,
      @RequestBody @Valid StudyRelationshipFormDto dto
  ) {

    LOGGER.info(
        String.format(
            "Creating new study relationship for study %s: type=%s targetStudy=%s",
            sourceStudyId, dto.getType(), dto.getTargetStudyId()));
    Study sourceStudy = this.getStudyFromIdentifier(sourceStudyId);
    Study targetStudy = this.getStudyFromIdentifier(dto.getTargetStudyId().toString());

    StudyRelationship studyRelationship = this.createNewStudyRelationship(sourceStudy, targetStudy, dto.getType());

    return new ResponseEntity<>(
        this.getStudyRelationshipMapper().toDetails(studyRelationship), HttpStatus.CREATED);
  }

  @DeleteMapping("/{relationshipId}")
  public HttpEntity<?> deleteStudyRelationship(
      @PathVariable("relationshipId") Long relationshipId
  ) {

    StudyRelationship relationship = this.getStudyRelationshipService().findById(relationshipId)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find study relationship: " + relationshipId));

    Study sourceStudy = getStudyFromIdentifier(relationship.getSourceStudy().getId().toString());
    Study targetStudy = getStudyFromIdentifier(relationship.getTargetStudy().getId().toString());

    this.deleteStudyRelationship(sourceStudy, targetStudy);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
