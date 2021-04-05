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

package com.decibeltx.studytracker.test.core.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.events.dto.StudyView;
import com.decibeltx.studytracker.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.EventType;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.service.ActivityService;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class ActivityServiceTests {

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ActivityService activityService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  private static final int ACTION_COUNT = 2;

  @Test
  public void addStudyActivityTest() {

    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    List<Activity> activityList = activityService.findByStudy(study);
    for (Activity activity : activityList) {
      System.out.println(activity.getEventType());
    }
    Assert.assertEquals(ACTION_COUNT, activityList.size());
    Activity activity = StudyActivityUtils
        .fromStudyStatusChange(study, study.getLastModifiedBy(), Status.IN_PLANNING,
            Status.COMPLETE);

    activityService.create(activity);

    Assert.assertNotNull(activity.getId());
    activityList = activityService.findByStudy(study);
    Assert.assertEquals(ACTION_COUNT + 1, activityList.size());

    activity = activityList.get(ACTION_COUNT);
    Assert.assertEquals(study.getCode(), ((StudyView) activity.getData().get("study")).getCode());
    Assert.assertEquals(study.getCreatedBy().getUsername(), activity.getUser().getUsername());
    Assert.assertEquals(EventType.STUDY_STATUS_CHANGED, activity.getEventType());
    Assert.assertEquals(Status.COMPLETE.toString(), activity.getData().get("newStatus"));

  }

//  @Test
//  public void findStudyActivityTest() {
//    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
//    Study study2 = studyRepository.findByCode("PPB-10001")
//        .orElseThrow(RecordNotFoundException::new);
//
//    List<Activity> activities = activityService.findByStudy(study);
//    Assert.assertEquals(2, activities.size());
//    activities = activityService.findByStudy(study2);
//    Assert.assertEquals(4, activities.size());
//
//    activities = activityService.findByProgram(study.getProgram());
//    Assert.assertEquals(4, activities.size());
//    activities = activityService.findByProgram(study2.getProgram());
//    Assert.assertEquals(6, activities.size());
//
//    activities = activityService.findByEventType(EventType.NEW_STUDY);
//    Assert.assertEquals(6, activities.size());
//    activities = activityService.findByEventType(EventType.DELETED_STUDY);
//    Assert.assertEquals(0, activities.size());
//
//  }

}
