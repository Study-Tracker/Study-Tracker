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

package com.decibeltx.studytracker.test.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Collaborator;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.CollaboratorRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.service.NamingService;
import com.decibeltx.studytracker.service.StudyService;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
public class StudyServiceTests {

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyService studyService;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CollaboratorRepository collaboratorRepository;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Autowired
  private NamingService namingService;

  private static final int STUDY_COUNT = 6;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAllStudiesTest() {
    List<Study> studies = studyService.findAll();
    Assert.assertNotNull(studies);
    Assert.assertTrue(!studies.isEmpty());
    Assert.assertEquals(STUDY_COUNT, studies.size());
  }

  @Test
  public void findByCode() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals("PPB-10001", study.getCode());
  }

  @Test
  public void findByProgramTest() {
    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    List<Study> studies = studyService.findByProgram(program);
    Assert.assertTrue(!studies.isEmpty());
    Assert.assertEquals(2, studies.size());

    program = programRepository.findByName("Preclinical Project B")
        .orElseThrow(RecordNotFoundException::new);
    studies = studyService.findByProgram(program);
    Assert.assertTrue(!studies.isEmpty());
    Assert.assertEquals(2, studies.size());

    program = programRepository.findByName("Target ID Project D")
        .orElseThrow(RecordNotFoundException::new);
    studies = studyService.findByProgram(program);
    Assert.assertTrue(!studies.isEmpty());
    Assert.assertEquals(1, studies.size());

    program = programRepository.findByName("Cancelled Program C")
        .orElseThrow(RecordNotFoundException::new);
    studies = studyService.findByProgram(program);
    Assert.assertTrue(studies.isEmpty());
  }

  @Test
  public void createStudyTest() {
    Optional<Program> optionalProgram = programRepository.findByName("Clinical Program A");
    Assert.assertTrue(optionalProgram.isPresent());
    Program program = optionalProgram.get();
    Optional<User> optionalUser = userRepository.findByUsername("jsmith");
    Assert.assertTrue(optionalUser.isPresent());
    User user = optionalUser.get();
    Study study = new Study();
    study.setStatus(Status.IN_PLANNING);
    study.setName("Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setActive(true);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));
    studyService.create(study);
    Assert.assertEquals(STUDY_COUNT + 1, studyRepository.count());
    List<Study> studies = studyService.findByName("Study X");
    Assert.assertTrue(!studies.isEmpty());
    Study created = studies.get(0);
    Assert.assertNotNull(created.getId());
    Assert.assertNotNull(created.getCode());
    Assert.assertNotNull(created.getCreatedAt());
    Assert.assertNotNull(created.getUpdatedAt());
  }

  @Test
  public void studyUpdateTest() {
    Optional<Study> optional = studyService.findByCode("CPA-10001");
    Assert.assertTrue(optional.isPresent());
    Study study = optional.get();
    Date now = new Date();
    Assert.assertEquals(Status.IN_PLANNING, study.getStatus());
    Assert.assertNull(study.getEndDate());
    study.setStatus(Status.COMPLETE);
    study.setEndDate(now);
    studyService.update(study);
    Study updated = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Status.COMPLETE, updated.getStatus());
    Assert.assertEquals(now, updated.getEndDate());
  }

  @Test
  public void createStudyCodeTest() {

    Program program = programRepository.findByName("Clinical Program A")
        .orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setProgram(program);
    String code = namingService.generateStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("CPA-10003", code);

    program = programRepository.findByName("Preclinical Project B")
        .orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setProgram(program);
    code = namingService.generateStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("PPB-10002", code);

    program = programRepository.findByName("Target ID Project D")
        .orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setProgram(program);
    code = namingService.generateStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("TID-10003", code);

    program = programRepository.findByName("Cancelled Program C")
        .orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setProgram(program);
    code = namingService.generateStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("CPC-10001", code);

    Exception exception = null;
    study = new Study();
    try {
      code = namingService.generateStudyCode(study);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);

  }

  @Test
  public void createExternalStudyCodeTest() {
    Collaborator collaborator = collaboratorRepository.findByLabel("Inactive CRO")
        .orElseThrow(RecordNotFoundException::new);
    Study study = new Study();
    study.setCollaborator(collaborator);
    String code = namingService.generateExternalStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("IN-00001", code);

    collaborator = collaboratorRepository.findByLabel("University of Somewhere")
        .orElseThrow(RecordNotFoundException::new);
    study = new Study();
    study.setCollaborator(collaborator);
    code = namingService.generateExternalStudyCode(study);
    Assert.assertNotNull(code);
    Assert.assertEquals("US-00002", code);
  }

  @Test
  public void inactivateStudyTest() {
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(study.isActive());
    studyService.delete(study);
    Study updated = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(updated.isActive());
  }

  @Test
  public void updateStudyStatusTest() {
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Status.IN_PLANNING, study.getStatus());
    studyService.updateStatus(study, Status.COMPLETE);
    Study updated = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(Status.COMPLETE, updated.getStatus());
  }

  @Test
  public void studyCountTest() {

    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MONTH, -1);
    Date monthAgo = calendar.getTime();

    Assert.assertEquals(STUDY_COUNT, studyService.count());
    Assert.assertEquals(0, studyService.countBeforeDate(monthAgo));
    Assert.assertEquals(STUDY_COUNT, studyService.countFromDate(monthAgo));
    Assert.assertEquals(STUDY_COUNT, studyService.countBetweenDates(monthAgo, now));

  }

}
