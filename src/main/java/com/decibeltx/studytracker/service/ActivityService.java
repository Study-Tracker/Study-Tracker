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


import com.decibeltx.studytracker.events.EventType;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.ActivityRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

  @Autowired
  private ActivityRepository activityRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ProgramRepository programRepository;

  public List<Activity> findAll() {
    return activityRepository.findAll();
  }

  public List<Activity> findAll(Sort sort) {
    return activityRepository.findAll(sort);
  }

  public Page<Activity> findAll(Pageable pageable) {
    return activityRepository.findAll(pageable);
  }

  public Optional<Activity> findById(Long id) {
    return activityRepository.findById(id);
  }

  public List<Activity> findByStudy(Study study) {
    return activityRepository.findByStudyId(study.getId());
  }

  public List<Activity> findByAssay(Assay assay) {
    return activityRepository.findByAssayId(assay.getId());
  }

  public List<Activity> findByProgram(Program program) {
    return activityRepository.findByProgramId(program.getId());
  }

  public List<Activity> findByEventType(EventType type) {
    return activityRepository.findByEventType(type);
  }

  public List<Activity> findByUser(User user) {
    return activityRepository.findByUserId(user.getId());
  }

  @Transactional
  public Activity create(Activity activity) {
    if (activity.getAssay() != null && activity.getStudy() == null) {
      Study study = studyRepository.findByAssayId(activity.getAssay().getId())
          .orElseThrow(() -> new RecordNotFoundException("Could not find study: " + activity.getAssay().getId()));
      activity.setStudy(study);
    }
    if (activity.getStudy() != null && activity.getProgram() == null) {
      Program program = programRepository.findByStudyId(activity.getStudy().getId())
          .orElseThrow(() -> new RecordNotFoundException("Could not find program: " + activity.getStudy().getId()));
      activity.setProgram(program);
    }
    return activityRepository.save(activity);
  }

  @Transactional
  public void delete(Activity activity) {
    if (activityRepository.existsById(activity.getId())) {
      activityRepository.delete(activity);
    }
    throw new RecordNotFoundException("Activity record not found: " + activity.getId().toString());
  }

  @Transactional
  public void deleteStudyActivity(Study study) {
    for (Activity activity : this.findByStudy(study)) {
      this.delete(activity);
    }
  }

  public long count() {
    return activityRepository.count();
  }

  public long countFromDate(Date startDate) {
    return activityRepository.countByDateAfter(startDate);
  }

  public long countBeforeDate(Date endDate) {
    return activityRepository.countByDateBefore(endDate);
  }

  public long countBetweenDates(Date startDate, Date endDate) {
    return activityRepository.countByDateBetween(startDate, endDate);
  }

  public long countCompletedStudiesFromDate(Date date) {
    return activityRepository.findStatusChangeStudiesAfterDate(date)
        .stream()
        .filter(a -> a.getData().containsKey("newStatus") && a.getData().get("newStatus").equals("COMPLETE"))
        .count();
  }

}
