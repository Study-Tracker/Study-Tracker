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

package io.studytracker.repository;

import io.studytracker.model.StudyCollection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyCollectionRepository extends JpaRepository<StudyCollection, Long> {

  @Override
  @EntityGraph("study-collection-summary")
  List<StudyCollection> findAll();

  @Override
  @EntityGraph("study-collection-details")
  Optional<StudyCollection> findById(Long id);

  @EntityGraph("study-collection-summary")
  @Query("select c from StudyCollection c where c.createdBy.id = ?1")
  List<StudyCollection> findByCreatedById(Long id);

  @Query("select c from StudyCollection c where c.createdBy.id = ?1")
  Page<StudyCollection> findByCreatedById(Long id, Pageable pageable);

  @EntityGraph("study-collection-summary")
  List<StudyCollection> findByStudiesId(Long id);

  Page<StudyCollection> findByStudiesId(Long id, Pageable pageable);
}
