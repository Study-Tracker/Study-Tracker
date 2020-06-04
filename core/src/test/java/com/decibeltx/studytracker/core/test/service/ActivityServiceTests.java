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

package com.decibeltx.studytracker.core.test.service;

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.events.type.StudyEvent;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.ActivityService;
import com.decibeltx.studytracker.core.test.TestConfiguration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
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
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("oldStatus", Status.IN_PLANNING);
    data.put("newStatus", Status.COMPLETE);
    StudyEvent event = new StudyEvent(this, study, study.getLastModifiedBy(),
        EventType.STUDY_STATUS_CHANGED, data);
    Activity activity = Activity.from(event);

    activityService.create(activity);

    Assert.assertNotNull(activity.getId());
    activityList = activityService.findByStudy(study);
    Assert.assertEquals(ACTION_COUNT + 1, activityList.size());

    activity = activityList.get(ACTION_COUNT);
    Assert.assertEquals(study.getCode(), activity.getData().get("code"));
    Assert.assertEquals(study.getCreatedBy().getAccountName(), activity.getUser().getAccountName());
    Assert.assertEquals(EventType.STUDY_STATUS_CHANGED, activity.getEventType());
    Assert.assertEquals(Status.COMPLETE.toString(), activity.getData().get("newStatus"));

  }

  @Test
  public void findStudyActivityTest() {
    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Study study2 = studyRepository.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);

    List<Activity> activities = activityService.findByStudy(study);
    Assert.assertEquals(2, activities.size());
    activities = activityService.findByStudy(study2);
    Assert.assertEquals(4, activities.size());

    activities = activityService.findByProgram(study.getProgram());
    Assert.assertEquals(4, activities.size());
    activities = activityService.findByProgram(study2.getProgram());
    Assert.assertEquals(6, activities.size());

    activities = activityService.findByEventType(EventType.NEW_STUDY);
    Assert.assertEquals(6, activities.size());
    activities = activityService.findByEventType(EventType.DELETED_STUDY);
    Assert.assertEquals(0, activities.size());

  }

}
