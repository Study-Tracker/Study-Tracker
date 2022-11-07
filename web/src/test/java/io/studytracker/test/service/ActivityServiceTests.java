/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.test.service;

import io.studytracker.Application;
import io.studytracker.events.EventType;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Activity;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.repository.StudyRepository;
import io.studytracker.service.ActivityService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

  private static final int ACTION_COUNT = 2;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ActivityService activityService;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addStudyActivityTest() {

    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    List<Activity> activityList = activityService.findByStudy(study);
    for (Activity activity : activityList) {
      System.out.println(activity.getEventType());
    }
    Assert.assertEquals(ACTION_COUNT, activityList.size());
    Activity activity =
        StudyActivityUtils.fromStudyStatusChange(
            study, study.getLastModifiedBy(), Status.IN_PLANNING, Status.COMPLETE);

    activityService.create(activity);

    Assert.assertNotNull(activity.getId());
    activityList = activityService.findByStudy(study).stream()
        .sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
        .collect(Collectors.toList());
    Assert.assertEquals(ACTION_COUNT + 1, activityList.size());

    activity = activityList.get(ACTION_COUNT);
    Assert.assertEquals(
        study.getCode(), ((Map<String, Object>) activity.getData().get("study")).get("code"));
    Assert.assertEquals(study.getCreatedBy().getEmail(), activity.getUser().getEmail());
    Assert.assertEquals(EventType.STUDY_STATUS_CHANGED, activity.getEventType());
    Assert.assertEquals(Status.COMPLETE.toString(), activity.getData().get("newStatus"));
  }

  //  @Test
  //  public void findStudyActivityTest() {
  //    Study study =
  // studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
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
