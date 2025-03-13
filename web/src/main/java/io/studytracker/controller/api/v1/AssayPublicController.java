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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractAssayController;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.AssayDto;
import io.studytracker.mapstruct.dto.api.AssayPayloadDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayOptions;
import io.studytracker.model.AssayType;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Set;
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
@RequestMapping("/api/v1/assay")
public class AssayPublicController extends AbstractAssayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayPublicController.class);

  @GetMapping("")
  public Page<AssayDto> findAll(Pageable pageable) {
    LOGGER.debug("Finding all Assays");
    Page<Assay> page = this.getAssayService().findAll(pageable);
    return new PageImpl<>(this.getAssayMapper().toAssayDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public AssayDto findById(@PathVariable Long id) {
    LOGGER.debug("Finding Assay with id: {}", id);
    Assay assay = this.getAssayService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Assay not found: " + id));
    return this.getAssayMapper().toAssayDto(assay);
  }

  private void mapPayloadFields(Assay assay, AssayPayloadDto dto) {

    // Get the study
    Study study = this.getStudyService().findById(dto.getStudyId())
        .orElseThrow(() -> new InvalidConstraintException("Cannot find study: " + dto.getStudyId()));
    assay.setStudy(study);

    // Get the assay type
    AssayType assayType = this.getAssayTypeService().findById(dto.getAssayTypeId())
        .orElseThrow(() -> new InvalidConstraintException("Cannot find assay type: " + dto.getAssayTypeId()));
    assay.setAssayType(assayType);

    // Get the owner and users
    assay.setOwner(
        this.getUserService()
            .findById(dto.getOwner())
            .orElseThrow(
                () ->
                    new InvalidConstraintException("Cannot find user: " + dto.getOwner())));

    Set<User> team = new HashSet<>();
    for (Long id : dto.getUsers()) {
      team.add(
          this.getUserService().findById(id)
              .orElseThrow(() -> new InvalidConstraintException("Cannot find user: " + id)));
    }
    assay.setUsers(team);

  }

  @PostMapping("")
  public HttpEntity<AssayDto> create(@Valid @RequestBody AssayPayloadDto dto) {
    LOGGER.info("Creating new assay: {}", dto);
    Assay assay = this.getAssayMapper().fromPayload(dto);
    this.mapPayloadFields(assay, dto);
    AssayOptions options = this.getAssayMapper().optionsFromAssayPayload(dto);
    User user = this.getAuthenticatedUser();
    Assay created = this.createAssay(assay, assay.getStudy(), user, options);
    return new ResponseEntity<>(this.getAssayMapper().toAssayDto(created), HttpStatus.CREATED);

  }

  @PutMapping("/{id}")
  public HttpEntity<AssayDto> update(
      @PathVariable Long id, @Valid @RequestBody AssayPayloadDto dto) {
    LOGGER.info("Updating existing assay: {}", dto);
    Assay assay = this.getAssayMapper().fromPayload(dto);
    this.mapPayloadFields(assay, dto);
    User user = this.getAuthenticatedUser();
    Assay updated = this.updateAssay(assay, user);
    return new ResponseEntity<>(this.getAssayMapper().toAssayDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Deleting assay: {}", id);
    this.deleteAssay(id.toString(), this.getAuthenticatedUser());
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
