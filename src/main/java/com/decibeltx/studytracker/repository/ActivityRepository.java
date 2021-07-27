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

import com.decibeltx.studytracker.events.EventType;
import com.decibeltx.studytracker.model.Activity;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

  @Override
  @EntityGraph("activity-details")
  Page<Activity> findAll(Pageable pageable);

  @Override
  @EntityGraph("activity-details")
  List<Activity> findAll();

  @Override
  @EntityGraph("activity-details")
  Optional<Activity> findById(Long id);

  @EntityGraph("activity-details")
  List<Activity> findByEventType(EventType eventType);

  @EntityGraph("activity-details")
  @Query("select a from Activity a where a.study.id = ?1 ")
  List<Activity> findByStudyId(Long studyId);

  @EntityGraph("activity-details")
  @Query("select a from Activity a where a.assay.id = ?1 ")
  List<Activity> findByAssayId(Long assayId);

  @EntityGraph("activity-details")
  @Query("select a from Activity a where a.program.id = ?1 ")
  List<Activity> findByProgramId(Long programId);

  @EntityGraph("activity-details")
  @Query("select a from Activity a where a.user.id = ?1 ")
  List<Activity> findByUserId(Long userId);

  long countByDateAfter(Date date);

  long countByDateBefore(Date date);

  long countByDateBetween(Date startDate, Date endDate);

  @Query("select a from Activity a where a.eventType = 'STUDY_STATUS_CHANGED' and a.date >= ?1")
  List<Activity> findStatusChangeStudiesAfterDate(Date date);

}
