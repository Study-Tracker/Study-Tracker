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

package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.RelationshipType;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyRelationship;
import com.decibeltx.studytracker.repository.StudyRelationshipRepository;
import java.util.List;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyRelationshipService {

  @Autowired
  private StudyRelationshipRepository studyRelationshipRepository;

  public Optional<StudyRelationship> findById(Long id) {
    return studyRelationshipRepository.findById(id);
  }

  @Transactional(readOnly = true)
  public List<StudyRelationship> findStudyRelationships(Study study) {
    List<StudyRelationship> relationships = studyRelationshipRepository.findBySourceStudyId(study.getId());
    for (StudyRelationship relationship: relationships) {
      Hibernate.initialize(relationship.getSourceStudy());
      Hibernate.initialize(relationship.getTargetStudy());
    }
    return relationships;
  }

  @Transactional
  public StudyRelationship addStudyRelationship(Study sourceStudy, Study targetStudy, RelationshipType type) {

    StudyRelationship sourceRelationship;
    StudyRelationship targetRelationship;

    Optional<StudyRelationship> optional = studyRelationshipRepository
        .findBySourceAndTargetStudyIds(sourceStudy.getId(), targetStudy.getId());
    if (optional.isPresent()) {
      sourceRelationship = optional.get();
      sourceRelationship.setType(type);
      targetRelationship = studyRelationshipRepository
          .findBySourceAndTargetStudyIds(targetStudy.getId(), sourceStudy.getId())
          .orElseThrow(RecordNotFoundException::new);
      targetRelationship.setType(RelationshipType.getInverse(type));
    } else {
      sourceRelationship = new StudyRelationship(type, sourceStudy, targetStudy);
      targetRelationship = new StudyRelationship(RelationshipType.getInverse(type), targetStudy, sourceStudy);
    }

    studyRelationshipRepository.save(sourceRelationship);
    studyRelationshipRepository.save(targetRelationship);

    return sourceRelationship;

  }

  @Transactional
  public void removeStudyRelationship(Study sourceStudy, Study targetStudy) {

    Optional<StudyRelationship> optional = studyRelationshipRepository.findBySourceAndTargetStudyIds(
        sourceStudy.getId(), targetStudy.getId());
    if (optional.isPresent()) {
      StudyRelationship relationship = optional.get();
      sourceStudy.removeStudyRelationship(relationship);
      targetStudy.removeStudyRelationship(relationship);
      studyRelationshipRepository.deleteById(relationship.getId());
    } else {
      throw new RecordNotFoundException(String.format("No study relationship found for source study "
          + "%s and target study %s", sourceStudy.getCode(), targetStudy.getCode()));
    }

    optional = studyRelationshipRepository.findBySourceAndTargetStudyIds(
        targetStudy.getId(), sourceStudy.getId());
    if (optional.isPresent()) {
      StudyRelationship relationship = optional.get();
      sourceStudy.removeStudyRelationship(relationship);
      targetStudy.removeStudyRelationship(relationship);
      studyRelationshipRepository.deleteById(relationship.getId());
    } else {
      throw new RecordNotFoundException(String.format("No study relationship found for source study "
          + "%s and target study %s", targetStudy.getCode(), sourceStudy.getCode()));
    }

  }

}
