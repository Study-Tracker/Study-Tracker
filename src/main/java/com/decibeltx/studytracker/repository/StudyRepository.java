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

package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyRepository extends JpaRepository<Study, Long> {

  @Override
  @EntityGraph("study-summary")
  List<Study> findAll();

  @EntityGraph("study-with-attributes")
  @Query("select s from Study s")
  List<Study> findAllWithDetails();

  @Override
  @EntityGraph("study-with-attributes")
  Optional<Study> findById(Long id);

  @EntityGraph("study-with-attributes")
  Optional<Study> findByCode(String code);

  @EntityGraph("study-with-attributes")
  Optional<Study> findByExternalCode(String code);

  @Query("select s from Study s where s.program.id = ?1")
  List<Study> findByProgramId(Long programId);

  @EntityGraph("study-with-attributes")
  List<Study> findByName(String name);

  @Query("select s from Study s where s.program.id = ?1 and s.legacy = false")
  List<Study> findActiveProgramStudies(Long programId);

  @Query("select s from Assay a join a.study s where a.id = ?1")
  Optional<Study> findByAssayId(Long assayId);

  @EntityGraph("study-with-attributes")
  List<Study> findByUsersId(Long userId);

  /**
   * Fetches all studies that have an {@code externalCode} value that starts with the provided
   * prefix. Used in generating additional externa study codes.
   *
   * @param prefix
   * @return
   */
  @Query("select s from Study s where lower(s.externalCode) like lower(concat(?1, '%'))")
  List<Study> findByExternalCodePrefix(String prefix);

  @Query("select s from Study s where lower(s.name) like lower(concat('%', ?1, '%')) or lower(s.code) like lower(concat('%', ?1, '%'))")
  List<Study> findByNameOrCodeLike(String keyword);

  @EntityGraph("study-with-attributes")
  List<Study> findByUpdatedAtAfter(Date date);

  long countByCreatedAtBefore(Date date);

  long countByCreatedAtAfter(Date date);

  long countByCreatedAtBetween(Date startDate, Date endDate);

  long countByProgram(Program program);

  long countByProgramAndCreatedAtAfter(Program program, Date date);

  @Query(nativeQuery = true, value = "select count(distinct(s.id)) "
      + "from studies s join study_users su on s.id = su.study_id "
      + "where (s.created_by = ?1 or su.user_id = ?1) and s.status in ('IN_PLANNING', 'ACTIVE') "
      + "and s.active = true")
  long countActiveUserStudies(Long userId);

  @Query(nativeQuery = true, value = "select count(distinct(s.id)) "
      + "from studies s join study_users su on s.id = su.study_id "
      + "where (s.created_by = ?1 or su.user_id = ?1) "
      + "and s.status in ('COMPLETE') "
      + "and s.active = true")
  long countCompleteUserStudies(Long userId);

}
