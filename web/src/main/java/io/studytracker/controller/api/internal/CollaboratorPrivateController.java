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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractCollaboratorController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.CollaboratorFormDto;
import io.studytracker.mapstruct.dto.response.CollaboratorDetailsDto;
import io.studytracker.model.Collaborator;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@RequestMapping("/api/internal/collaborator")
public class CollaboratorPrivateController extends AbstractCollaboratorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorPrivateController.class);

  @GetMapping("")
  public List<CollaboratorDetailsDto> getAllExternalContacts(
      @RequestParam(value = "label", required = false) String label,
      @RequestParam(value = "organizationName", required = false) String name,
      @RequestParam(value = "organizationCode", required = false) String code) {
    List<Collaborator> collaborators;
    if (label != null) {
      Optional<Collaborator> optional = this.getCollaboratorService().findByLabel(label);
      collaborators =
          optional.isPresent() ? Collections.singletonList(optional.get()) : new ArrayList<>();
    } else if (name != null) {
      collaborators = this.getCollaboratorService().findByOrganizationName(name);
    } else if (code != null) {
      collaborators = this.getCollaboratorService().findByCode(code);
    } else {
      collaborators = this.getCollaboratorService().findAll();
    }
    return this.getCollaboratorMapper().toDetailsDtoList(collaborators);
  }

  @GetMapping("/{id}")
  public CollaboratorDetailsDto getExternalContact(@PathVariable("id") Long id) throws Exception {
    Optional<Collaborator> optional = this.getCollaboratorService().findById(id);
    if (optional.isPresent()) {
      return this.getCollaboratorMapper().toDetailsDto(optional.get());
    } else {
      throw new RecordNotFoundException();
    }
  }

  @PostMapping("")
  public HttpEntity<CollaboratorDetailsDto> createCollaborator(
      @Valid @RequestBody CollaboratorFormDto dto) {
    LOGGER.info("Creating new collaborator record: " + dto.toString());
    Collaborator collaborator = this.createNewCollaborator(this.getCollaboratorMapper().fromFormDto(dto));
    LOGGER.info(collaborator.toString());
    return new ResponseEntity<>(this.getCollaboratorMapper().toDetailsDto(collaborator), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<CollaboratorDetailsDto> updateCollaborator(
      @PathVariable("id") Long id, @Valid @RequestBody CollaboratorFormDto dto) {
    LOGGER.info("Updating collaborator record: " + dto.toString());
    Collaborator collaborator = this.getCollaboratorMapper().fromFormDto(dto);
    collaborator.setId(id);
    collaborator = this.updateExistingCollaborator(collaborator, id);
    return new ResponseEntity<>(this.getCollaboratorMapper().toDetailsDto(collaborator), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> removeCollaborator(@PathVariable("id") Long id) {
    LOGGER.info("Removing collaborator record: " + id);
    this.deleteCollaborator(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
