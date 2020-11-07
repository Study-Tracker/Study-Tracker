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

package com.decibeltx.studytracker.core.repository;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.EventType;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ActivityRepository extends MongoRepository<Activity, String> {

  List<Activity> findByEventType(EventType eventType);

  @Query("{ 'reference': 'STUDY', 'referenceId': ?0 }")
  List<Activity> findByStudyId(String studyId);

  @Query("{ 'reference': 'ASSAY', 'referenceId': ?0 }")
  List<Activity> findByAssayId(String assayId);

  @Query("{ 'reference': 'PROGRAM', 'referenceId': ?0 }")
  List<Activity> findByProgramId(String programId);

  @Query("{ 'user.id': ?0 }")
  List<Activity> findByUserId(String userId);

}
