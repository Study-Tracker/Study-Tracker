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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StatusPayloadDto;
import io.studytracker.mapstruct.dto.api.StudyDto;
import io.studytracker.mapstruct.dto.api.StudyPayloadDto;
import io.studytracker.model.Keyword;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/v1/study")
public class StudyPublicController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyPublicController.class);

  @GetMapping("")
  public Page<StudyDto> findAll(Pageable pageable) {
    LOGGER.debug("Find all studies");
    Page<Study> studies = this.getStudyService().findAll(pageable);
    return new PageImpl<>(this.getStudyMapper().toDtoList(studies.getContent()), pageable, studies.getTotalElements());
  }

  @GetMapping("/{id}")
  public StudyDto findById(@PathVariable Long id) {
    LOGGER.debug("Find study by id: {}", id);
    Study study = this.getStudyService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + id));
    return this.getStudyMapper().toDto(study);
  }

  private void mapPayloadFields(Study study, StudyPayloadDto dto) {
    // Get the users
    Set<User> team = new HashSet<>();
    for (Long id : dto.getUsers()) {
      team.add(
          this.getUserService().findById(id)
              .orElseThrow(() -> new InvalidConstraintException("Cannot find user: " + id)));
    }
    study.setUsers(team);

    // Get the owner
    study.setOwner(
        this.getUserService()
            .findById(dto.getOwner())
            .orElseThrow(
                () ->
                    new InvalidConstraintException("Cannot find user: " + dto.getOwner())));

    // Get the program
    study.setProgram(
        this.getProgramService()
            .findById(dto.getProgramId())
            .orElseThrow(
                () ->
                    new InvalidConstraintException("Cannot find program: " + dto.getProgramId())));

    // Get the collaborator
    if (dto.getCollaboratorId() != null) {
      study.setCollaborator(
          this.getCollaboratorService()
              .findById(dto.getCollaboratorId())
              .orElseThrow(
                  () ->
                      new InvalidConstraintException("Cannot find user: " + dto.getCollaboratorId())));
    }

    // Get the keywords
    Set<Keyword> keywords = new HashSet<>();
    for (Long id: dto.getKeywords()) {
      keywords.add(
          this.getKeywordService()
              .findById(id)
              .orElseThrow(() -> new InvalidConstraintException("Cannot find keyword: " + id)));
    }
    study.setKeywords(keywords);
  }

  @PostMapping("")
  public HttpEntity<StudyDto> create(@Valid @RequestBody StudyPayloadDto dto) {
    LOGGER.info("Creating new study: {}", dto);
    Study study = this.getStudyMapper().fromPayload(dto);
    mapPayloadFields(study, dto);
    study = this.createNewStudy(study, dto.getNotebookTemplateId());
    return new ResponseEntity<>(this.getStudyMapper().toDto(study), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyDto> updateStudy(
      @PathVariable Long id, @Valid @RequestBody StudyPayloadDto dto) {
    LOGGER.info("Updating study: {}", dto);
    this.getStudyService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + id));
    Study study = this.getStudyMapper().fromPayload(dto);
    mapPayloadFields(study, dto);
    study = this.updateExistingStudy(study);
    return new ResponseEntity<>(this.getStudyMapper().toDto(study), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteStudy(@PathVariable Long id) {
    LOGGER.info("Deleting study with id: {}", id);
    Study study = this.getStudyService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + id));
    this.deleteExistingStudy(study);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{id}/status")
  public HttpEntity<?> updateStudyStatus(
      @PathVariable Long id,
      @RequestBody StatusPayloadDto dto
  ) {
    LOGGER.info("Updating study status: {}", dto.getStatus());
    Study study =
        this.getStudyService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + id));
    this.updateExistingStudyStatus(study, dto.getStatus());
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
