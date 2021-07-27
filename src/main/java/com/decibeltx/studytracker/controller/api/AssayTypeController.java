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

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.AssayTypeDetailsDto;
import com.decibeltx.studytracker.mapstruct.mapper.AssayTypeMapper;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.service.AssayTypeService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/assaytype")
public class AssayTypeController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypeController.class);

  @Autowired
  private AssayTypeService assayTypeService;

  @Autowired
  private AssayTypeMapper assayTypeMapper;

  @GetMapping("")
  public List<AssayTypeDetailsDto> findAll() {
    return assayTypeMapper.toDetailsDtoList(assayTypeService.findAll());
  }

  @GetMapping("/{id}")
  public AssayTypeDetailsDto findById(@PathVariable("id") Long assayTypeId) throws RecordNotFoundException {
    Optional<AssayType> optional = assayTypeService.findById(assayTypeId);
    if (optional.isPresent()) {
      return assayTypeMapper.toDetailsDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<AssayTypeDetailsDto> create(@RequestBody @Valid AssayTypeDetailsDto dto) {
    LOGGER.info("Creating assay type");
    LOGGER.info(dto.toString());
    AssayType assayType = assayTypeMapper.fromDetailsDto(dto);
    assayType.setActive(true);
    assayTypeService.create(assayType);
    return new ResponseEntity<>(assayTypeMapper.toDetailsDto(assayType), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<AssayType> update(@PathVariable("id") String id,
      @RequestBody @Valid AssayTypeDetailsDto dto) {
    LOGGER.info("Updating assay type");
    LOGGER.info(dto.toString());
    AssayType assayType = assayTypeMapper.fromDetailsDto(dto);
    assayTypeService.update(assayType);
    return new ResponseEntity<>(assayType, HttpStatus.OK);
  }

  @PatchMapping("/{id}")
  public HttpEntity<?> toggleActive(@PathVariable("id") Long id) {
    AssayType assayType = assayTypeService.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay type: " + id));
    assayTypeService.toggleActive(assayType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") Long id) {
    LOGGER.info("Deleting assay type: " + id);
    AssayType assayType = assayTypeService.findById(id).orElseThrow(RecordNotFoundException::new);
    assayTypeService.delete(assayType);
  }

}
