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

package io.studytracker.test.service;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.model.User;
import io.studytracker.repository.UserRepository;
import io.studytracker.service.StudyConclusionsService;
import io.studytracker.service.StudyService;
import java.util.Date;
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
public class StudyConclusionsServiceTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private StudyConclusionsService studyConclusionsService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addConclusionsTest() {
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertNull(study.getConclusions());

    User user = userRepository.findAll().get(0);

    StudyConclusions conclusions = new StudyConclusions();
    conclusions.setContent("This is a test");
    conclusions.setCreatedBy(study.getCreatedBy());
    conclusions.setStudy(study);
    conclusions.setCreatedBy(user);
    conclusions.setLastModifiedBy(user);

    studyConclusionsService.addStudyConclusions(study, conclusions);
    Assert.assertNotNull(conclusions.getId());
    Assert.assertNotNull(conclusions.getCreatedAt());

    study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(study.getConclusions());

    Optional<StudyConclusions> optional = studyConclusionsService.findStudyConclusions(study);
    Assert.assertTrue(optional.isPresent());
    conclusions = optional.get();
    Assert.assertEquals("This is a test", conclusions.getContent());

  }

  @Test
  public void updateConclusionsTest() {

    addConclusionsTest();

    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);

    study.setLastModifiedBy(study.getCreatedBy());

    StudyConclusions conclusions = study.getConclusions();
    Date firstDate = conclusions.getCreatedAt();
    Assert.assertEquals(conclusions.getCreatedAt(), conclusions.getUpdatedAt());
    conclusions.setContent("Different text");
    conclusions.setLastModifiedBy(conclusions.getCreatedBy());
    studyConclusionsService.updateStudyConclusions(study, conclusions);

    conclusions = studyConclusionsService.findStudyConclusions(study)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(conclusions.getUpdatedAt());
    Assert.assertNotEquals(firstDate, conclusions.getUpdatedAt());
    Assert.assertEquals("Different text", conclusions.getContent());
  }

  @Test
  public void deleteConclusionsTest() {
    addConclusionsTest();
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    studyConclusionsService.deleteStudyConclusions(study);
    Exception exception = null;
    try {
      studyConclusionsService.findStudyConclusions(study)
          .orElseThrow(RecordNotFoundException::new);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordNotFoundException);
  }

}
