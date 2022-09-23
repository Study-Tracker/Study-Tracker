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

import io.studytracker.controller.api.AbstractStudyConclusionsController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyConclusionsDto;
import io.studytracker.mapstruct.dto.api.StudyConclusionsPayloadDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
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
@RequestMapping("/api/v1/conclusions")
public class ConclusionsPublicController extends AbstractStudyConclusionsController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConclusionsPublicController.class);

  @GetMapping("")
  public Page<StudyConclusionsDto> findAll(Pageable pageable) {
    LOGGER.debug("Finding all study conclusions");
    Page<StudyConclusions> page = this.getStudyConclusionsService().findAll(pageable);
    return new PageImpl<>(this.getConclusionsMapper().toDtoList(page.getContent()), pageable,
        page.getTotalElements());
  }

  @GetMapping("/{id}")
  public StudyConclusionsDto findById(@PathVariable Long id) {
    LOGGER.debug("Finding study conclusions by id: {}", id);
    StudyConclusions conclusions = this.getStudyConclusionsService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study conclusions not found: " + id));
    return this.getConclusionsMapper().toDto(conclusions);
  }

  @PostMapping("")
  public HttpEntity<StudyConclusionsDto> create(@Valid @RequestBody StudyConclusionsPayloadDto dto) {
    LOGGER.info("Creating study conclusions: {}", dto);
    Study study = this.getStudyService().findById(dto.getStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Study not found: " + dto.getStudyId()));
    StudyConclusions conclusions =
        this.createNewConclusions(study, this.getConclusionsMapper().fromPayload(dto));
    return new ResponseEntity<>(this.getConclusionsMapper().toDto(conclusions), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<?> update(@PathVariable Long id, @Valid @RequestBody StudyConclusionsPayloadDto dto) {
    LOGGER.info("Updating study conclusions: {}", dto);
    Study study = this.getStudyService().findById(dto.getStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Study not found: " + dto.getStudyId()));
    StudyConclusions updated =
        this.updateExistingConclusions(study, this.getConclusionsMapper().fromPayload(dto));
    return new ResponseEntity<>(this.getConclusionsMapper().toDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Deleting study conclusions: {}", id);
    StudyConclusions conclusions = this.getStudyConclusionsService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study conclusions not found: " + id));
    this.deleteStudyConclusions(conclusions.getStudy());
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
