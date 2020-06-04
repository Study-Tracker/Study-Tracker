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

package com.decibeltx.studytracker.core.test;

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.repository.ActivityRepository;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.repository.UserRepository;
import com.decibeltx.studytracker.core.service.StudyService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class StudyEventTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private ActivityRepository activityRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  private Study generateStudy() {
    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    Optional<User> optionalUser = userRepository.findByAccountName("jsmith");
    Assert.assertTrue(optionalUser.isPresent());
    User user = optionalUser.get();
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(true);
    study.setCode(program.getCode() + "-9000");
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singletonList(user));
    return study;
  }

  @Test
  public void newStudyEventTest() {
    studyService.create(generateStudy());
    Study study = studyRepository.findByCode("CPA-9000").orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(study.getId());
    List<Activity> activityList = activityRepository.findByStudyId(study.getId());
    Assert.assertFalse(activityList.isEmpty());
    Assert.assertEquals(1, activityList.size());
    Activity activity = activityList.get(0);
    Assert.assertEquals(study.getId(), activity.getReferenceId());
    Assert.assertNotNull(study.getStorageFolder());
  }

  @Test
  public void statusChangeEventTest() {
    newStudyEventTest();
    Study study = studyRepository.findByCode("CPA-9000").orElseThrow(RecordNotFoundException::new);
    studyService.updateStatus(study, Status.COMPLETE);
    study = studyRepository.findByCode("CPA-9000").orElseThrow(RecordNotFoundException::new);
    List<Activity> activityList = activityRepository.findByStudyId(study.getId());
    Assert.assertFalse(activityList.isEmpty());
    Assert.assertEquals(2, activityList.size());
    Activity activity = activityList.get(1);
    Assert.assertEquals(EventType.STUDY_STATUS_CHANGED, activity.getEventType());
    Assert.assertFalse(activity.getData().isEmpty());
    Assert.assertTrue(activity.getData().containsKey("newStatus"));
    Assert.assertEquals(Status.COMPLETE.toString(), activity.getData().get("newStatus"));
  }

}
