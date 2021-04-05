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

import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for reading and writing {@link Study} records.
 */
public interface StudyService {

  /**
   * Finds a single study, identified by its primary key ID
   *
   * @param id pkid
   * @return optional study
   */
  Optional<Study> findById(String id);

  /**
   * Returns all study records.
   *
   * @return all studies
   */
  List<Study> findAll();

  /**
   * Finds all studies associated with a given {@link Program}
   *
   * @param program program object
   * @return list of studies
   */
  List<Study> findByProgram(Program program);

  /**
   * Finds a study with the unique given name.
   *
   * @param name study name, unique
   * @return optional study
   */
  List<Study> findByName(String name);

  /**
   * Finds a study by its unique internal code.
   *
   * @param code internal code
   * @return optional study
   */
  Optional<Study> findByCode(String code);

  /**
   * Finds a study by its optional, unique internal code.
   *
   * @param code internal code
   * @return optional study
   */
  Optional<Study> findByExternalCode(String code);

  /**
   * Creates a new study record
   *
   * @param study new study
   */
  void create(Study study);

  /**
   * Updates an existing study.
   *
   * @param study existing study
   */
  void update(Study study);

  /**
   * Deletes the given study, identifies by its primary key ID.
   *
   * @param study study to be deleted
   */
  void delete(Study study);

  /**
   * Updates the status of the study with the provided PKID to the provided status.
   *
   * @param study  study
   * @param status status to set
   */
  void updateStatus(Study study, Status status);

  /**
   * Searches the study repository using the provided keyword and returns matching {@link Study}
   * records.
   *
   * @param keyword
   * @return
   */
  List<Study> search(String keyword);

  /**
   * Counting number of studies created before/after/between given dates.
   */
  long count();

  long countFromDate(Date startDate);

  long countBeforeDate(Date endDate);

  long countBetweenDates(Date startDate, Date endDate);

}
