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
import com.decibeltx.studytracker.mapstruct.dto.CollaboratorDto;
import com.decibeltx.studytracker.mapstruct.mapper.CollaboratorMapper;
import com.decibeltx.studytracker.model.Collaborator;
import com.decibeltx.studytracker.service.CollaboratorService;
import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/collaborator")
public class CollaboratorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorController.class);

  @Autowired
  private CollaboratorService collaboratorService;

  @Autowired
  private CollaboratorMapper collaboratorMapper;

  @GetMapping("")
  public List<CollaboratorDto> getAllExternalContacts(
      @RequestParam(value = "label", required = false) String label,
      @RequestParam(value = "organizationName", required = false) String name,
      @RequestParam(value = "organizationCode", required = false) String code
  ) {
    List<Collaborator> collaborators;
    if (label != null) {
      Optional<Collaborator> optional = collaboratorService.findByLabel(label);
      collaborators = optional.isPresent() ? Collections.singletonList(optional.get()) : new ArrayList<>();
    } else if (name != null) {
      collaborators = collaboratorService.findByOrganizationName(name);
    } else if (code != null) {
      collaborators = collaboratorService.findByCode(code);
    } else {
      collaborators = collaboratorService.findAll();
    }
    return collaboratorMapper.toDtoList(collaborators);
  }

  @GetMapping("/{id}")
  public CollaboratorDto getExternalContact(@PathVariable("id") Long id) throws Exception {
    Optional<Collaborator> optional = collaboratorService.findById(id);
    if (optional.isPresent()) {
      return collaboratorMapper.toDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<CollaboratorDto> createNewCollaborator(
      @Valid @RequestBody CollaboratorDto dto) {
    LOGGER.info("Creating new collaborator record: " + dto.toString());
    Collaborator collaborator = collaboratorMapper.fromDto(dto);
    collaboratorService.create(collaborator);
    LOGGER.info(collaborator.toString());
    return new ResponseEntity<>(collaboratorMapper.toDto(collaborator), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<CollaboratorDto> updateCollaborator(@PathVariable("id") Long id,
      @Valid @RequestBody CollaboratorDto dto) {
    LOGGER.info("Updating collaborator record: " + dto.toString());
    if (!collaboratorService.exists(id)) {
      throw new RecordNotFoundException("Collaborator does not exist: " + id);
    }
    Collaborator collaborator = collaboratorMapper.fromDto(dto);
    collaborator.setId(id);
    collaboratorService.update(collaborator);
    LOGGER.info(collaborator.toString());
    return new ResponseEntity<>(collaboratorMapper.toDto(collaborator), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> removeCollaborator(@PathVariable("id") Long id) {
    LOGGER.info("Removing collaborator record: " + id);
    if (!collaboratorService.exists(id)) {
      throw new RecordNotFoundException("Collaborator does not exist: " + id);
    }
    collaboratorService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
