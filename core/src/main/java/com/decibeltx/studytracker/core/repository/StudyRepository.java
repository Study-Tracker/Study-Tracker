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

import com.decibeltx.studytracker.core.model.Study;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface StudyRepository extends MongoRepository<Study, String> {

  Optional<Study> findByCode(String code);

  Optional<Study> findByExternalCode(String code);

  @Query("{ 'program.id': ?0 }")
  List<Study> findByProgramId(String programId);

  List<Study> findByName(String name);

  @Query("{ 'program.id': ?0, 'legacy': false }")
  List<Study> findActiveProgramStudies(String programId);

  /**
   * Fetches all studies that have an {@code externalCode} value that starts with the provided
   * prefix. Used in generating additional externa study codes.
   *
   * @param prefix
   * @return
   */
  @Query("{ 'externalCode': { '$regex': ?0, '$options': 'i' } }")
  List<Study> findByExternalCodePrefix(String prefix);

  @Query("{ $or: [{ name: { '$regex': ?0, '$options': 'i'  }}, { code: { '$regex': ?0, '$options': 'i'  }} ] }")
  List<Study> findByNameOrCodeLike(String keyword);

  long countByCreatedAtBefore(Date date);

  long countByCreatedAtAfter(Date date);

  long countByCreatedAtBetween(Date startDate, Date endDate);

}
