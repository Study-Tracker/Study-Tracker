/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.controller.api.AbstractStudyConclusionsController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.mapstruct.dto.form.StudyConclusionsFormDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDetailsDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/internal/study/{studyId}/conclusions")
@RestController
public class StudyConclusionsPrivateController extends AbstractStudyConclusionsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyConclusionsPrivateController.class);

  @GetMapping("")
  public StudyConclusionsDetailsDto getStudyConclusions(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    Optional<StudyConclusions> optional = this.getStudyConclusionsService().findStudyConclusions(study);
    if (optional.isPresent()) {
      return this.getConclusionsMapper().toDetailsDto(optional.get());
    }
    throw new RecordNotFoundException("No conclusions found for study: " + study.getCode());
  }

  @PostMapping("")
  public HttpEntity<StudyConclusionsDetailsDto> newStudyConclusions(
      @PathVariable("studyId") String studyId,
      @RequestBody @Valid StudyConclusionsFormDto dto
  ) {
    Study study = getStudyFromIdentifier(studyId);
    if (dto.getId() != null || study.getConclusions() != null) {
      throw new StudyTrackerException("Study conclusions object already exists.");
    }
    LOGGER.info(String.format("Creating conclusions for study %s: %s", studyId, dto));
    StudyConclusions conclusions =
        this.createNewConclusions(study, this.getConclusionsMapper().fromFormDto(dto));
    return new ResponseEntity<>(this.getConclusionsMapper().toDetailsDto(conclusions), HttpStatus.CREATED);
  }

  @PutMapping("")
  public HttpEntity<StudyConclusionsDetailsDto> editStudyConclusions(
      @PathVariable("studyId") String studyId,
      @RequestBody @Valid StudyConclusionsFormDto dto
  ) {
    LOGGER.info(String.format("Updating conclusions for study %s: %s", studyId, dto.toString()));
    Study study = getStudyFromIdentifier(studyId);
    StudyConclusions updated = this.updateExistingConclusions(study, this.getConclusionsMapper().fromFormDto(dto));
    return new ResponseEntity<>(this.getConclusionsMapper().toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("")
  public HttpEntity<?> delete(@PathVariable("studyId") String studyId) {
    LOGGER.info(String.format("Deleting conclusions for study %s", studyId));
    Study study = getStudyFromIdentifier(studyId);
    this.deleteStudyConclusions(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
