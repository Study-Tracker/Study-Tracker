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

package com.decibeltx.studytracker.teams.test;

import com.decibeltx.studytracker.core.example.ExampleDataGenerator;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Message;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.teams.TeamsMessageUtils;
import com.decibeltx.studytracker.teams.TeamsMessagingService;
import com.decibeltx.studytracker.teams.entity.DriveItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class MessagingServiceTests {

  private static final Resource EXAMPLE_FILE = new ClassPathResource("test.txt");

  @Autowired
  private TeamsMessagingService messagingService;

  @Autowired
  private StudyService studyService;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void newStudyMessageTest() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    Message message = messagingService.sendStudyMessage(
        TeamsMessageUtils.newStudyMessage(study), study);
    Assert.assertNotNull(message);
    Assert.assertNotNull(message.getUrl());
  }

  @Test
  public void newStudyMessageWithAttachmentTest() throws Exception {
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    DriveItem item = messagingService.uploadStudyFile(study, EXAMPLE_FILE);
    Message message = messagingService.sendStudyMessage(
        TeamsMessageUtils.newStudyMessage(study, item.getWebUrl()), study);
    Assert.assertNotNull(message);
    Assert.assertNotNull(message.getUrl());
  }

}
