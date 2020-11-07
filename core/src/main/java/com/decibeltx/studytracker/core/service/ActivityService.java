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

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface ActivityService {

  List<Activity> findAll();

  List<Activity> findAll(Sort sort);

  Page<Activity> findAll(Pageable pageable);

  Optional<Activity> findById(String id);

  List<Activity> findByStudy(Study study);

  List<Activity> findByAssay(Assay assay);

  List<Activity> findByProgram(Program program);

  List<Activity> findByEventType(EventType type);

  List<Activity> findByUser(User user);

  Activity create(Activity activity);

  Activity update(Activity activity);

  void delete(Activity activity);

  void deleteStudyActivity(Study study);

}
