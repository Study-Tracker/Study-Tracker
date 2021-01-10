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

package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.StudyRelationship;
import com.decibeltx.studytracker.core.model.StudyRelationship.Type;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.StudyRelationshipService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyRelationshipServiceImpl implements StudyRelationshipService {

  @Autowired
  private StudyRepository studyRepository;

  @Override
  public List<StudyRelationship> getStudyRelationships(Study study) {
    return study.getStudyRelationships();
  }

  @Override
  public void addStudyRelationship(Study sourceStudy, Study targetStudy, Type type) {
    StudyRelationship sourceRelationship = new StudyRelationship(type, targetStudy);
    Type targetType = Type.getInverse(type);
    StudyRelationship targetRelationship = new StudyRelationship(targetType, sourceStudy);

    List<StudyRelationship> sourceRelationships = sourceStudy.getStudyRelationships()
        .stream()
        .filter(r -> !r.getStudy().getId().equals(targetStudy.getId()))
        .collect(Collectors.toList());
    sourceRelationships.add(sourceRelationship);
    sourceStudy.setStudyRelationships(sourceRelationships);

    List<StudyRelationship> targetRelationships = targetStudy.getStudyRelationships()
        .stream()
        .filter(r -> !r.getStudy().getId().equals(sourceStudy.getId()))
        .collect(Collectors.toList());
    targetRelationships.add(targetRelationship);
    targetStudy.setStudyRelationships(targetRelationships);

    studyRepository.save(sourceStudy);
    studyRepository.save(targetStudy);

  }

  @Override
  public void removeStudyRelationship(Study sourceStudy, Study targetStudy) {
    List<StudyRelationship> sourceRelationships = sourceStudy.getStudyRelationships()
        .stream()
        .filter(r -> !r.getStudy().getId().equals(targetStudy.getId()))
        .collect(Collectors.toList());
    sourceStudy.setStudyRelationships(sourceRelationships);

    List<StudyRelationship> targetRelationships = targetStudy.getStudyRelationships()
        .stream()
        .filter(r -> !r.getStudy().getId().equals(sourceStudy.getId()))
        .collect(Collectors.toList());
    targetStudy.setStudyRelationships(targetRelationships);

    studyRepository.save(sourceStudy);
    studyRepository.save(targetStudy);

  }

}
