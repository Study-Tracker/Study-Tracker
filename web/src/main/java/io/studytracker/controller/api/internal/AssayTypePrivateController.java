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

import io.studytracker.controller.api.AbstractAssayTypeController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.AssayTypeFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeDetailsDto;
import io.studytracker.model.AssayType;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/assaytype")
public class AssayTypePrivateController extends AbstractAssayTypeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypePrivateController.class);

  @GetMapping("")
  public List<AssayTypeDetailsDto> findAll() {
    return this.getAssayTypeMapper().toDetailsDtoList(this.getAssayTypeService().findAllWithDetails());
  }

  @GetMapping("/{id}")
  public AssayTypeDetailsDto findById(@PathVariable("id") Long assayTypeId)
      throws RecordNotFoundException {
    Optional<AssayType> optional = this.getAssayTypeService().findById(assayTypeId);
    if (optional.isPresent()) {
      return this.getAssayTypeMapper().toDetailsDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<AssayTypeDetailsDto> create(@RequestBody @Valid AssayTypeFormDto dto) {
    LOGGER.info("Creating assay type: {}", dto);
    AssayType created = this.createNewAssayType(this.getAssayTypeMapper().fromFormDto(dto));
    LOGGER.info("New assay type: {}", created);
    return new ResponseEntity<>(this.getAssayTypeMapper().toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<AssayType> update(
      @PathVariable("id") Long id, @RequestBody @Valid AssayTypeFormDto dto) {
    LOGGER.info("Updating assay type: {}", dto);
    Optional<AssayType> optional = this.getAssayTypeService().findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException();
    }
    AssayType assayType = this.updateExistingAssayType(this.getAssayTypeMapper().fromFormDto(dto));
    LOGGER.info("Updated assay type: {}", assayType);
    return new ResponseEntity<>(assayType, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> toggleActive(@PathVariable("id") Long id) {
    AssayType assayType =
        this.getAssayTypeService()
            .findById(id)
            .orElseThrow(() -> new RecordNotFoundException("Cannot find assay type: " + id));
    this.getAssayTypeService().toggleActive(assayType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting assay type: " + id);
    AssayType assayType = this.getAssayTypeService()
        .findById(id).orElseThrow(RecordNotFoundException::new);
    this.deleteAssayType(assayType);
  }
}
