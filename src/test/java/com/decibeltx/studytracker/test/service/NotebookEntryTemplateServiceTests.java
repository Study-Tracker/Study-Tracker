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
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.model.NotebookEntryTemplate.Category;
import com.decibeltx.studytracker.repository.NotebookEntryTemplateRepository;
import com.decibeltx.studytracker.service.NotebookEntryTemplateService;
import java.util.List;
import java.util.Optional;
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
public class NotebookEntryTemplateServiceTests {

  @Autowired
  private NotebookEntryTemplateService templateService;

  @Autowired
  private NotebookEntryTemplateRepository templateRepository;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAllTemplatesTest() {
    List<NotebookEntryTemplate> templates = templateService.findAll();
    Assert.assertNotNull(templates);
    Assert.assertEquals(templates.size(), ExampleDataGenerator.NOTEBOOK_ENTRY_TEMPLATE_COUNT);
  }

  @Test
  public void defaultTemplateTests() {

    // Get the default templates from the full list
    List<NotebookEntryTemplate> templates = templateService.findAll();
    Assert.assertEquals(ExampleDataGenerator.NOTEBOOK_ENTRY_TEMPLATE_COUNT, 5);

    List<NotebookEntryTemplate> defaultTemplates = templates.stream()
        .filter(NotebookEntryTemplate::isDefault)
        .collect(Collectors.toList());
    Assert.assertEquals(defaultTemplates.size(), 2);

    NotebookEntryTemplate studyDefault = defaultTemplates.stream()
        .filter(t -> t.getCategory().equals(Category.STUDY))
        .findFirst().get();
    Assert.assertEquals("Default Study Template", studyDefault.getName());

    NotebookEntryTemplate assayDefault = defaultTemplates.stream()
        .filter(t -> t.getCategory().equals(Category.ASSAY))
        .findFirst().get();
    Assert.assertEquals("Default Assay Template", assayDefault.getName());

    // Test against repository method
    Optional<NotebookEntryTemplate> optional = templateRepository.findDefaultByCategory(Category.STUDY);
    Assert.assertTrue(optional.isPresent());
    NotebookEntryTemplate template = optional.get();
    Assert.assertTrue(template.isDefault());
    Assert.assertEquals(studyDefault.getId(), template.getId());

    optional = templateRepository.findDefaultByCategory(Category.ASSAY);
    Assert.assertTrue(optional.isPresent());
    template = optional.get();
    Assert.assertTrue(template.isDefault());
    Assert.assertEquals(assayDefault.getId(), template.getId());

    // Update the defaults

    NotebookEntryTemplate newStudyDefault = templates.stream()
        .filter(t -> t.getCategory().equals(Category.STUDY) && !t.isDefault() && t.isActive())
        .findFirst().get();
    Assert.assertEquals("Active Study Template", newStudyDefault.getName());

    NotebookEntryTemplate newAssayDefault = templates.stream()
        .filter(t -> t.getCategory().equals(Category.ASSAY) && !t.isDefault() && t.isActive())
        .findFirst().get();
    Assert.assertEquals("Active Assay Template", newAssayDefault.getName());

    templateService.updateDefault(newStudyDefault);
    templateService.updateDefault(newAssayDefault);

    optional = templateRepository.findDefaultByCategory(Category.STUDY);
    Assert.assertTrue(optional.isPresent());
    template = optional.get();
    Assert.assertTrue(template.isDefault());
    Assert.assertEquals(newStudyDefault.getName(), template.getName());

    optional = templateRepository.findDefaultByCategory(Category.ASSAY);
    Assert.assertTrue(optional.isPresent());
    template = optional.get();
    Assert.assertTrue(template.isDefault());
    Assert.assertEquals(newAssayDefault.getName(), template.getName());

  }

}
