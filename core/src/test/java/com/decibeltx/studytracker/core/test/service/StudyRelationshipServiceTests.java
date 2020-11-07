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

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.StudyRelationship.Type;
import com.decibeltx.studytracker.core.service.StudyRelationshipService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.test.TestConfiguration;
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
public class StudyRelationshipServiceTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyRelationshipService studyRelationshipService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void studyRelationshipTests() {

    Study sourceStudy = studyService.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study targetStudy = studyService.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(0, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(0, targetStudy.getStudyRelationships().size());

    studyRelationshipService.addStudyRelationship(sourceStudy, targetStudy, Type.IS_BLOCKING);

    Study updatedSource = studyService.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study updatedTarget = studyService.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(1, updatedSource.getStudyRelationships().size());
    Assert.assertEquals(1, updatedTarget.getStudyRelationships().size());
    Assert.assertEquals(updatedTarget.getCode(),
        updatedSource.getStudyRelationships().get(0).getStudy().getId());
    Assert.assertEquals(Type.IS_BLOCKING, updatedSource.getStudyRelationships().get(0).getType());
    Assert.assertEquals(updatedSource.getCode(),
        updatedTarget.getStudyRelationships().get(0).getStudy().getId());
    Assert.assertEquals(Type.IS_BLOCKED_BY, updatedTarget.getStudyRelationships().get(0).getType());

    studyRelationshipService.addStudyRelationship(sourceStudy, targetStudy, Type.IS_RELATED_TO);

    updatedSource = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    updatedTarget = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);

    Assert.assertEquals(1, updatedSource.getStudyRelationships().size());
    Assert.assertEquals(1, updatedTarget.getStudyRelationships().size());
    Assert.assertEquals(updatedTarget.getCode(),
        updatedSource.getStudyRelationships().get(0).getStudy().getId());
    Assert.assertEquals(Type.IS_RELATED_TO, updatedSource.getStudyRelationships().get(0).getType());
    Assert.assertEquals(updatedSource.getCode(),
        updatedTarget.getStudyRelationships().get(0).getStudy().getId());
    Assert.assertEquals(Type.IS_RELATED_TO, updatedTarget.getStudyRelationships().get(0).getType());

  }

  @Test
  public void removeStudyRelationshipTest() {
    this.studyRelationshipTests();
    Study sourceStudy = studyService.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study targetStudy = studyService.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, sourceStudy.getStudyRelationships().size());
    Assert.assertEquals(1, targetStudy.getStudyRelationships().size());
    studyRelationshipService.removeStudyRelationship(sourceStudy, targetStudy);
    Study updatedSource = studyService.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    Study updatedTarget = studyService.findByCode("PPB-10001")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, updatedSource.getStudyRelationships().size());
    Assert.assertEquals(0, updatedTarget.getStudyRelationships().size());
  }


}
