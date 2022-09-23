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

package io.studytracker.repository;

import io.studytracker.model.NotebookEntryTemplate;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Deprecated
public interface NotebookEntryTemplateRepository
    extends JpaRepository<NotebookEntryTemplate, Long> {

  @Override
  @EntityGraph("entry-template-details")
  Optional<NotebookEntryTemplate> findById(Long id);

  @Query("select t from NotebookEntryTemplate t where t.isDefault = true and t.category = ?1")
  Optional<NotebookEntryTemplate> findDefaultByCategory(NotebookEntryTemplate.Category category);
}
