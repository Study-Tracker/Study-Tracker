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
import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.service.StudyCommentService;
import com.decibeltx.studytracker.service.StudyService;
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
public class StudyCommentServiceTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyCommentService studyCommentService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addCommentTest() {
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(study.getComments().isEmpty());

    Comment comment = new Comment();
    comment.setText("This is a test");
    comment.setCreatedBy(study.getCreatedBy());
    studyCommentService.addStudyComment(study, comment);
    Assert.assertNotNull(comment.getId());
    Assert.assertNotNull(comment.getCreatedAt());
    Long id = comment.getId();

    study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.getComments().isEmpty());

    Optional<Comment> optional = studyCommentService.findStudyCommentById(id);
    Assert.assertTrue(optional.isPresent());
    comment = optional.get();
    Assert.assertEquals("This is a test", comment.getText());

  }

  @Test
  public void updateCommentTest() {

    addCommentTest();

    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);

    Comment comment = study.getComments().stream().findFirst().get();
    Long id = comment.getId();
    Date firstDate = comment.getCreatedAt();
    comment.setText("Different text");
    studyCommentService.updateStudyComment(comment);

    comment = studyCommentService.findStudyCommentById(id)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(comment.getUpdatedAt());
    Assert.assertNotEquals(firstDate, comment.getUpdatedAt());
    Assert.assertEquals("Different text", comment.getText());
  }

  @Test
  public void deleteCommentTest() {
    addCommentTest();
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    Comment comment = study.getComments().stream().findFirst().get();
    Long id = comment.getId();
    studyCommentService.deleteStudyComment(study, comment);
    Exception exception = null;
    try {
      comment = studyCommentService.findStudyCommentById(id)
          .orElseThrow(RecordNotFoundException::new);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordNotFoundException);
  }

}
