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

import io.studytracker.controller.api.AbstractStudyCollectionController;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.StudyCollectionFormDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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

@RestController
@RequestMapping("/api/internal/studycollection")
public class StudyCollectionPrivateController extends AbstractStudyCollectionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCollectionPrivateController.class);

  @GetMapping("")
  public List<StudyCollectionSummaryDto> getStudyCollections(
      @RequestParam(name = "userId", required = false) Long userId,
      @RequestParam(name = "studyId", required = false) Long studyId
  ) {

    List<StudyCollection> collections;
    User authenticatedUser = this.getAuthenticatedUser();
    if (userId != null) {
      User user =
          this.getUserService()
              .findById(userId)
              .orElseThrow(() -> new InvalidConstraintException("User does not exist: " + userId));
      collections = this.getStudyCollectionService().findByUser(user);
    } else if (studyId != null) {
      Study study =
          this.getStudyService()
              .findById(studyId)
              .orElseThrow(
                  () -> new InvalidConstraintException("Study does not exist: " + studyId));
      collections = this.getStudyCollectionService().findByStudy(study);
    } else {
      collections = this.getStudyCollectionService().findAll();
    }

    // Filter out private collections
    collections =
        collections.stream()
            .filter(
                c ->
                    c.isShared()
                        || c.getCreatedBy().getId().equals(authenticatedUser.getId())
                        || authenticatedUser.isAdmin())
            .collect(Collectors.toList());

    return this.getStudyCollectionMapper().toSummaryDtoList(collections);
  }

  @GetMapping("/{id}")
  public StudyCollectionDetailsDto findById(@PathVariable("id") Long id) {

    Optional<StudyCollection> optional = this.getStudyCollectionService().findById(id);
    User user = this.getAuthenticatedUser();

    if (optional.isPresent()) {
      StudyCollection collection = optional.get();
      if (collection.isShared()
          || collection.getCreatedBy().getId().equals(user.getId())
          || user.isAdmin()) {
        return this.getStudyCollectionMapper().toDetailsDto(collection);
      }
    }

    throw new RecordNotFoundException("Could not find study collection: " + id);
  }

  @PostMapping("")
  public HttpEntity<StudyCollectionSummaryDto> createCollection(
      @RequestBody @Valid StudyCollectionFormDto payload
  ) {
    LOGGER.info("Creating new study collections: " + payload.toString());
    StudyCollection collection =
        this.createNewStudyCollection(this.getStudyCollectionMapper().fromFormDto(payload));
    return new ResponseEntity<>(this.getStudyCollectionMapper().toSummaryDto(collection),
        HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<StudyCollectionSummaryDto> updateCollection(
      @PathVariable("id") Long id,
      @RequestBody @Valid StudyCollectionFormDto dto
  ) {

    LOGGER.info("Attempting to update existing study collections: " + dto.toString());

    // Make sure the collection exists
    StudyCollection existing = this.getStudyCollectionService()
        .findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Study collection not found: " + id));
    StudyCollection collection = this.getStudyCollectionMapper().fromFormDto(dto);
    User user = this.getAuthenticatedUser();

    // If collections is not public, only owner can edit
    if (!dto.isShared() && !user.getId().equals(existing.getCreatedBy().getId())) {
      throw new InsufficientPrivilegesException(
          "You do not have permission to modify this study collection.");
    }

    // Update the collection
    StudyCollection updated = this.updateExistingStudyCollection(collection);

    return new ResponseEntity<>(this.getStudyCollectionMapper().toSummaryDto(updated), HttpStatus.OK);
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
    this.addStudyToCollection(collectionId, studyId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{collectionId}/{studyId}")
  public HttpEntity<?> removeStudy(
      @PathVariable("collectionId") Long collectionId,
      @PathVariable("studyId") Long studyId
  ) {
    this.removeStudyFromCollection(collectionId, studyId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
