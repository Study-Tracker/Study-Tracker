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

package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AssayService {

  Optional<Assay> findById(String id);

  List<Assay> findByStudyId(String studyId);

  Optional<Assay> findByCode(String code);

  List<Assay> findAll();

  void create(Assay assay);

  void update(Assay assay);

  void delete(Assay assay);

  /**
   * Updates the status of the study with the provided PKID to the provided status.
   *
   * @param assay  assay
   * @param status status to set
   */
  void updateStatus(Assay assay, Status status);

  String generateAssayCode(Assay assay);

  /**
   * Counting number of assays created before/after/between given dates.
   */
  long count();

  long countFromDate(Date startDate);

  long countBeforeDate(Date endDate);

  long countBetweenDates(Date startDate, Date endDate);

}
