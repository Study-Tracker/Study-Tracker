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

package io.studytracker.repository;

import io.studytracker.model.Assay;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssayRepository extends JpaRepository<Assay, Long> {

  @Override
  @EntityGraph("assay-with-parents")
  List<Assay> findAll();

  @Override
  @EntityGraph("assay-with-attributes")
  Optional<Assay> findById(Long id);

  @EntityGraph("assay-with-attributes")
  Optional<Assay> findByCode(String code);

  @EntityGraph("assay-summary")
  List<Assay> findByStudyId(Long studyId);

  @Query("select a from Assay a where lower(a.code) like lower(concat(?1, '%'))")
  List<Assay> findByCodePrefix(String prefix);

  @Query("select count(a) from Assay a where lower(a.code) like lower(concat(?1, '%'))")
  long countByCodePrefix(String prefix);

  long countByCreatedAtBefore(Date date);

  long countByCreatedAtAfter(Date date);

  long countByCreatedAtBetween(Date startDate, Date endDate);
}
