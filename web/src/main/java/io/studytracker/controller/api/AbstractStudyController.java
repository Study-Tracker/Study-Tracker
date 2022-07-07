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

package io.studytracker.controller.api;

import io.studytracker.events.EventsService;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.mapper.ActivityMapper;
import io.studytracker.mapstruct.mapper.AssayMapper;
import io.studytracker.mapstruct.mapper.StudyMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.service.ActivityService;
import io.studytracker.service.AssayService;
import io.studytracker.service.ProgramService;
import io.studytracker.service.StudyService;
import io.studytracker.service.UserService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractStudyController {

  private StudyService studyService;

  private UserService userService;

  private ProgramService programService;

  private AssayService assayService;

  private ActivityService activityService;

  private EventsService eventsService;

  private StudyMapper studyMapper;

  private AssayMapper assayMapper;

  private ActivityMapper activityMapper;

  private boolean isLong(String value) {
    try {
      Long.parseLong(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  protected Study getStudyFromIdentifier(String id) {
    Optional<Study> optional;
    if (isLong(id)) {
      optional = studyService.findById(Long.parseLong(id));
    } else {
      optional = studyService.findByCode(id);
    }
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException("Cannot find study: " + id);
    }
  }

  protected Assay getAssayFromIdentifier(String id) {
    Optional<Assay> optional;
    if (isLong(id)) {
      optional = assayService.findById(Long.parseLong(id));
    } else {
      optional = assayService.findByCode(id);
    }
    if (optional.isPresent()) {
      return optional.get();
    } else {
      throw new RecordNotFoundException("Cannot find assay: " + id);
    }
  }

  public StudyService getStudyService() {
    return studyService;
  }

  @Autowired
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public UserService getUserService() {
    return userService;
  }

  @Autowired
  public void setUserService(UserService userService) {
    this.userService = userService;
  }

  public ProgramService getProgramService() {
    return programService;
  }

  @Autowired
  public void setProgramService(ProgramService programService) {
    this.programService = programService;
  }

  public AssayService getAssayService() {
    return assayService;
  }

  @Autowired
  public void setAssayService(AssayService assayService) {
    this.assayService = assayService;
  }

  public ActivityService getActivityService() {
    return activityService;
  }

  @Autowired
  public void setActivityService(ActivityService activityService) {
    this.activityService = activityService;
  }

  public EventsService getEventsService() {
    return eventsService;
  }

  @Autowired
  public void setEventsService(EventsService eventsService) {
    this.eventsService = eventsService;
  }

  public StudyMapper getStudyMapper() {
    return studyMapper;
  }

  @Autowired
  public void setStudyMapper(StudyMapper studyMapper) {
    this.studyMapper = studyMapper;
  }

  public AssayMapper getAssayMapper() {
    return assayMapper;
  }

  @Autowired
  public void setAssayMapper(AssayMapper assayMapper) {
    this.assayMapper = assayMapper;
  }

  public ActivityMapper getActivityMapper() {
    return activityMapper;
  }

  @Autowired
  public void setActivityMapper(ActivityMapper activityMapper) {
    this.activityMapper = activityMapper;
  }
}
