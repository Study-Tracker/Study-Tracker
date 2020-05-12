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

package com.decibeltx.studytracker.docwriter.test;

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.docwriter.StudySlideShowWriter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class StudySlideShowWriterTests {

  private static final Resource TEMPLATE = new ClassPathResource("study_template.pptx");

  @Autowired
  private Environment env;

  @Autowired
  private StudyService studyService;

  @Test
  public void createStudySlideShowNoTemplateTest() throws Exception {
    StudySlideShowWriter writer = new StudySlideShowWriter(
        Paths.get(env.getRequiredProperty("storage.temp-dir")), null);
    Study study = studyService.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);
    writer.writeStudySummaryDocument(study);
    Path path = Paths.get(env.getRequiredProperty("storage.temp-dir"));
    File file = path.resolve(study.getCode() + ".pptx").toFile();
    Assert.assertTrue(file.exists());
  }

  @Test
  public void createStudySlideShowWithTemplate() throws Exception {
    StudySlideShowWriter writer
        = new StudySlideShowWriter(Paths.get(env.getRequiredProperty("storage.temp-dir")),
        TEMPLATE);
    Study study = studyService.findByCode("PPB-10001").orElseThrow(RecordNotFoundException::new);
    writer.writeStudySummaryDocument(study);
    Path path = Paths.get(env.getRequiredProperty("storage.temp-dir"));
    File file = path.resolve(study.getCode() + ".pptx").toFile();
    Assert.assertTrue(file.exists());
  }

}
