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

package io.studytracker.service;

import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import io.studytracker.repository.StudyCollectionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudyCollectionService {

  @Autowired private StudyCollectionRepository studyCollectionRepository;

  public Optional<StudyCollection> findById(Long id) {
    return studyCollectionRepository.findById(id);
  }

  public Page<StudyCollection> findAll(Pageable pageable) {
    return studyCollectionRepository.findAll(pageable);
  }

  public List<StudyCollection> findAll() {
    return studyCollectionRepository.findAll();
  }

  public List<StudyCollection> findByUser(User user) {
    return studyCollectionRepository.findByCreatedById(user.getId());
  }

  public Page<StudyCollection> findByUser(User user, Pageable pageable) {
    return studyCollectionRepository.findByCreatedById(user.getId(), pageable);
  }

  public List<StudyCollection> findByStudy(Study study) {
    return studyCollectionRepository.findByStudiesId(study.getId());
  }

  public Page<StudyCollection> findByStudy(Study study, Pageable pageable) {
    return studyCollectionRepository.findByStudiesId(study.getId(), pageable);
  }

  public StudyCollection create(StudyCollection collection) {
    return studyCollectionRepository.save(collection);
  }

  public void update(StudyCollection collection) {
    StudyCollection c = studyCollectionRepository.getById(collection.getId());
    c.setDescription(collection.getDescription());
    c.setName(collection.getName());
    c.setStudies(collection.getStudies());
    c.setShared(collection.isShared());
    studyCollectionRepository.save(c);
  }

  public void delete(StudyCollection collection) {
    studyCollectionRepository.deleteById(collection.getId());
  }

  public boolean collectionWithNameExists(StudyCollection collection, User user) {
    return studyCollectionRepository.findByCreatedById(user.getId()).stream()
        .anyMatch(
            c ->
                c.getName().equalsIgnoreCase(collection.getName())
                    && !c.getId().equals(collection.getId()));
  }
}
