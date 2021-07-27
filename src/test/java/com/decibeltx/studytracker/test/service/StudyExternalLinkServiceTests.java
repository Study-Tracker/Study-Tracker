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
import com.decibeltx.studytracker.model.ExternalLink;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.service.StudyExternalLinkService;
import com.decibeltx.studytracker.service.StudyService;
import java.net.URL;
import java.util.Optional;
import org.hibernate.LazyInitializationException;
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
public class StudyExternalLinkServiceTests {

  @Autowired
  private StudyService studyService;

  @Autowired
  private StudyExternalLinkService externalLinkService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void addExternalLinkTest() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(study.getExternalLinks().isEmpty());

    ExternalLink link = new ExternalLink();
    link.setUrl(new URL("http://google.com"));
    link.setLabel("Google");
    externalLinkService.addStudyExternalLink(study, link);

    study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.getExternalLinks().isEmpty());
    link = study.getExternalLinks().stream().findFirst().orElseThrow();
    Assert.assertNotNull(link.getId());
    Long id = link.getId();

    study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(study.getComments().isEmpty());

    Optional<ExternalLink> optional = externalLinkService.findById(id);
    Assert.assertTrue(optional.isPresent());
    link = optional.get();
    Assert.assertEquals("Google", link.getLabel());
    Assert.assertNotNull(link.getStudy().getId());

    Exception exception = null;
    try {
      link.getStudy().getCode();
    } catch (Exception e) {
      exception = e;
      e.printStackTrace();
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof LazyInitializationException);

  }

  @Test
  public void updateExternalLinkTest() throws Exception {
    addExternalLinkTest();
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    ExternalLink link = study.getExternalLinks().stream().findFirst().get();
    Long id = link.getId();
    String url = link.getUrl().toString();
    link.setUrl(new URL("https://maps.google.com"));
    externalLinkService.updateStudyExternalLink(study, link);
    link = externalLinkService.findById(id)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotEquals(url, link.getUrl().toString());
  }

  @Test
  public void deleteExternalLinkTest() throws Exception {
    addExternalLinkTest();
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    ExternalLink link = study.getExternalLinks().stream().findFirst().get();
    Long id = link.getId();
    externalLinkService.deleteStudyExternalLink(study, link.getId());
    Exception exception = null;
    try {
      link = externalLinkService.findById(id)
          .orElseThrow(RecordNotFoundException::new);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof RecordNotFoundException);
  }

}
