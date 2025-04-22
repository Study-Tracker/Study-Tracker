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

import io.studytracker.controller.api.AbstractStudyCollectionController;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyCollectionDto;
import io.studytracker.mapstruct.dto.api.StudyCollectionPayloadDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/study-collection")
public class StudyCollectionPublicController extends AbstractStudyCollectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCollectionPublicController.class);

  @GetMapping("")
  public Page<StudyCollectionDto> findAll(
      @RequestParam(required = false) Long studyId,
      @RequestParam(required = false) Long userId,
      Pageable pageable
  ) {
    LOGGER.debug("Find all study collections");
    Page<StudyCollection> page;
    if (userId != null) {
      User user = this.getUserService().findById(userId)
          .orElseThrow(() -> new RecordNotFoundException("Cannot find user with ID: " + userId));
      page = this.getStudyCollectionService().findByUser(user, pageable);
    } else if (studyId != null) {
      Study study = this.getStudyService().findById(studyId)
          .orElseThrow(() -> new RecordNotFoundException("Cannot find study with ID: " + studyId));
      page = this.getStudyCollectionService().findByStudy(study, pageable);
    } else {
      page = this.getStudyCollectionService().findAll(pageable);
    }
    return new PageImpl<>(this.getStudyCollectionMapper().toDtoList(page.getContent()),
        pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public StudyCollectionDto findById(@PathVariable Long id) {
    LOGGER.debug("Find study collection by ID: " + id);
    StudyCollection collection = this.getStudyCollectionService().findById(id)
        .orElseThrow(() ->
            new RecordNotFoundException("Cannot find study collection with ID: " + id));
    return this.getStudyCollectionMapper().toDto(collection);
  }

  @PostMapping("")
  public HttpEntity<StudyCollectionDto> createCollection(
      @RequestBody @Valid StudyCollectionPayloadDto payload
  ) {

    LOGGER.info("Creating new study collections: " + payload.toString());
    StudyCollection collection = this.getStudyCollectionMapper().fromPayloadDto(payload);

    // Get the studies
    Set<Study> studySet = new HashSet<>();
    for (Long id: payload.getStudies()) {
      Study study = this.getStudyService().findById(id)
          .orElseThrow(() -> new InvalidConstraintException("Cannot find study with ID: " + id));
      studySet.add(study);
    }
    collection.setStudies(studySet);

    // Create the collection
    StudyCollection created = this.createNewStudyCollection(collection);

    return new ResponseEntity<>(this.getStudyCollectionMapper().toDto(created),
        HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyCollectionDto> updateCollection(
      @PathVariable("id") Long id,
      @RequestBody @Valid StudyCollectionPayloadDto dto
  ) {

    LOGGER.info("Attempting to update existing study collections: " + dto.toString());

    // Make sure the collection exists
    StudyCollection existing = this.getStudyCollectionService()
        .findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));
    StudyCollection collection = this.getStudyCollectionMapper().fromPayloadDto(dto);
    User user = this.getAuthenticatedUser();

    // If collections is not public, only owner can edit
    if (!existing.isShared() && !user.getId().equals(existing.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to modify this study collection.");
    }

    // Set the studies
    Set<Study> studySet = new HashSet<>();
    for (Long studyId: dto.getStudies()) {
      Study study = this.getStudyService().findById(studyId)
          .orElseThrow(() -> new InvalidConstraintException("Cannot find study with ID: " + studyId));
      studySet.add(study);
    }
    collection.setStudies(studySet);

    // Update the collection
    StudyCollection updated = this.updateExistingStudyCollection(collection);

    return new ResponseEntity<>(this.getStudyCollectionMapper().toDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteCollection(@PathVariable("id") Long id) {
    LOGGER.info("Attempting to delete study collection: " + id);
    this.deleteStudyCollection(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> addStudy(
      @PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId
  ) {
    LOGGER.info("Adding study {} to collection {}", studyId, collectionId);
    this.addStudyToCollection(collectionId, studyId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> removeStudy(
      @PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId
  ) {
    LOGGER.info("Removing study {} from collection {}", studyId, collectionId);
    this.removeStudyFromCollection(collectionId, studyId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
