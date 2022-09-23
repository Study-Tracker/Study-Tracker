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
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.RelationshipType;
import io.studytracker.model.Study;
import io.studytracker.model.StudyRelationship;
import io.studytracker.service.StudyRelationshipService;
import io.studytracker.service.StudyService;
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
public class StudyRelationshipServiceTests {

  @Autowired private StudyService studyService;

  @Autowired private StudyRelationshipService studyRelationshipService;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addStudyRelationshipTest() {

    Study sourceStudy =
        studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);

    Study targetStudy =
        studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);

    List<StudyRelationship> sourceStudyRelationships =
        studyRelationshipService.findStudyRelationships(sourceStudy);
    List<StudyRelationship> targetStudyRelationships =
        studyRelationshipService.findStudyRelationships(targetStudy);

    Assert.assertEquals(0, sourceStudyRelationships.size());
    Assert.assertEquals(0, targetStudyRelationships.size());

    studyRelationshipService.addStudyRelationship(
        sourceStudy, targetStudy, RelationshipType.IS_BLOCKING);

    //    Study updatedSource = studyService.findByCode("CPA-10001")
    //        .orElseThrow(RecordNotFoundException::new);
    //    Study updatedTarget = studyService.findByCode("PPB-10001")
    //        .orElseThrow(RecordNotFoundException::new);

    sourceStudyRelationships = studyRelationshipService.findStudyRelationships(sourceStudy);
    targetStudyRelationships = studyRelationshipService.findStudyRelationships(targetStudy);
    Assert.assertEquals(1, sourceStudyRelationships.size());
    Assert.assertEquals(1, targetStudyRelationships.size());

    StudyRelationship sourceRelationship = sourceStudyRelationships.get(0);
    StudyRelationship targetRelationship = targetStudyRelationships.get(0);
    Assert.assertEquals(targetStudy.getCode(), sourceRelationship.getTargetStudy().getCode());
    Assert.assertEquals(RelationshipType.IS_BLOCKING, sourceRelationship.getType());
    Assert.assertEquals(sourceStudy.getCode(), targetRelationship.getTargetStudy().getCode());
    Assert.assertEquals(RelationshipType.IS_BLOCKED_BY, targetRelationship.getType());

    studyRelationshipService.addStudyRelationship(
        sourceStudy, targetStudy, RelationshipType.IS_RELATED_TO);

    sourceStudyRelationships = studyRelationshipService.findStudyRelationships(sourceStudy);
    targetStudyRelationships = studyRelationshipService.findStudyRelationships(targetStudy);
    Assert.assertEquals(1, sourceStudyRelationships.size());
    Assert.assertEquals(1, targetStudyRelationships.size());
    sourceRelationship = sourceStudyRelationships.get(0);
    targetRelationship = targetStudyRelationships.get(0);

    Assert.assertEquals(targetStudy.getCode(), sourceRelationship.getTargetStudy().getCode());
    Assert.assertEquals(RelationshipType.IS_RELATED_TO, sourceRelationship.getType());
    Assert.assertEquals(sourceStudy.getCode(), targetRelationship.getTargetStudy().getCode());
    Assert.assertEquals(RelationshipType.IS_RELATED_TO, targetRelationship.getType());
  }

  @Test
  public void removeStudyRelationshipTest() {
    this.addStudyRelationshipTest();

    Study sourceStudy =
        studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Study targetStudy =
        studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    //    List<StudyRelationship> sourceRelationships
    //        = studyRelationshipService.findStudyRelationships(sourceStudy);
    //    List<StudyRelationship> targetRelationships
    //        = studyRelationshipService.findStudyRelationships(targetStudy);

    Assert.assertEquals(1, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());

    studyRelationshipService.removeStudyRelationship(sourceStudy, targetStudy);

    //    sourceRelationships = studyRelationshipService.findStudyRelationships(sourceStudy);
    //    targetRelationships = studyRelationshipService.findStudyRelationships(targetStudy);

    sourceStudy = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    targetStudy = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());
  }
}
