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

import io.studytracker.controller.api.AbstractCollaboratorController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.CollaboratorDto;
import io.studytracker.mapstruct.dto.api.CollaboratorPayloadDto;
import io.studytracker.model.Collaborator;
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
@RequestMapping("/api/v1/collaborator")
public class CollaboratorPublicController extends AbstractCollaboratorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorPublicController.class);

  @GetMapping("")
  public Page<CollaboratorDto> findAll(Pageable pageable) {
    LOGGER.debug("Find all collaborators");
    Page<Collaborator> page = this.getCollaboratorService().findAll(pageable);
    return new PageImpl<>(this.getCollaboratorMapper().toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public CollaboratorDto findById(Long id) {
    LOGGER.debug("Find collaborator with id {}", id);
    Collaborator collaborator = this.getCollaboratorService().findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Collaborator not found: " + id));
    return this.getCollaboratorMapper().toDto(collaborator);
  }

  @PostMapping("")
  public HttpEntity<CollaboratorDto> create(@Valid @RequestBody CollaboratorPayloadDto dto) {
    LOGGER.info("Creating collaborator: {}", dto);
    Collaborator collaborator = this.createNewCollaborator(this.getCollaboratorMapper().fromPayloadDto(dto));
    return new ResponseEntity<>(this.getCollaboratorMapper().toDto(collaborator), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<CollaboratorDto> update(
      @PathVariable Long id,
      @Valid @RequestBody CollaboratorPayloadDto dto
  ) {
    LOGGER.info("Updating existing collaborator: {}", dto);
    Collaborator collaborator = this.getCollaboratorMapper().fromPayloadDto(dto);
    collaborator.setId(id);
    collaborator = this.updateExistingCollaborator(collaborator, id);
    return new ResponseEntity<>(this.getCollaboratorMapper().toDto(collaborator), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> delete(@PathVariable Long id) {
    LOGGER.info("Deleting collaborator: {}", id);
    this.deleteCollaborator(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
