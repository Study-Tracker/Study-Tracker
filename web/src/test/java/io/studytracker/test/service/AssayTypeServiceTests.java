/*
 * Copyright 2019-2023 the original author or authors.
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
import io.studytracker.example.ExampleAssayTypeGenerator;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.model.AssayType;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.AssayTypeTask;
import io.studytracker.model.AssayTypeTaskField;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.repository.AssayTypeTaskFieldRepository;
import io.studytracker.service.AssayTypeService;
import java.util.Arrays;
import java.util.HashSet;
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
public class AssayTypeServiceTests {

  private static final int ASSAY_TYPE_COUNT = ExampleAssayTypeGenerator.ASSAY_TYPE_COUNT;

  @Autowired private AssayTypeService assayTypeService;

  @Autowired
  private AssayTypeTaskFieldRepository assayTypeTaskFieldRepository;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void findAllTest() {
    List<AssayType> assayTypes = assayTypeService.findAll();
    Assert.assertNotNull(assayTypes);
    Assert.assertTrue(!assayTypes.isEmpty());
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypes.size());
  }

  @Test
  public void findByNameTest() {
    Optional<AssayType> optional = assayTypeService.findByName("Generic");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Generic", optional.get().getName());

    optional = assayTypeService.findByName("Bad");
    Assert.assertFalse(optional.isPresent());
  }

  @Test
  public void createAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    assayType.addField(field);
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    Assert.assertFalse(optional.get().getFields().isEmpty());
  }

  @Test
  public void createAssayTypeWithTaskFieldsTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");

    AssayTypeTask task = new AssayTypeTask();
    task.setLabel("Step 1");
    task.setOrder(0);
    task.setStatus(TaskStatus.TODO);
    task.setAssayType(assayType);
    AssayTypeTaskField field = new AssayTypeTaskField();
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(0);
    field.setFieldName("field-1");
    field.setDisplayName("Field 1");
    field.setRequired(true);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setAssayTypeTask(task);
    task.addField(field);
    assayType.addTask(task);

    task = new AssayTypeTask();
    task.setLabel("Step 2");
    task.setOrder(1);
    task.setAssayType(assayType);
    task.setStatus(TaskStatus.TODO);
    field = new AssayTypeTaskField();
    field.setType(CustomEntityFieldType.INTEGER);
    field.setFieldOrder(0);
    field.setFieldName("field-a");
    field.setDisplayName("Field A");
    field.setRequired(false);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setAssayTypeTask(task);
    task.addField(field);
    assayType.addTask(task);

    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    AssayType created = optional.get();
    Assert.assertEquals("Test", created.getName());
    Assert.assertFalse(created.getTasks().isEmpty());
    Assert.assertEquals(2, created.getTasks().size());

    AssayTypeTask task1 = created.getTasks().stream()
        .filter(t -> t.getLabel().equals("Step 1")).findFirst().get();
    Assert.assertEquals("Step 1", task1.getLabel());
    Assert.assertEquals(0, task1.getOrder().intValue());
    List<AssayTypeTaskField> list = assayTypeTaskFieldRepository.findByAssayTypeTaskId(task1.getId());
    Assert.assertEquals(1, list.size());
    AssayTypeTaskField taskField = list.get(0);
    Assert.assertEquals("field-1", taskField.getFieldName());
    Assert.assertEquals("Field 1", taskField.getDisplayName());
    Assert.assertEquals(CustomEntityFieldType.STRING, taskField.getType());
    Assert.assertEquals(0, taskField.getFieldOrder().intValue());
    Assert.assertTrue(taskField.isRequired());
    Assert.assertTrue(taskField.isActive());
    Assert.assertEquals("This is a test", taskField.getDescription());

    AssayTypeTask task2 = created.getTasks().stream()
        .filter(t -> t.getLabel().equals("Step 2")).findFirst().get();
    Assert.assertEquals("Step 2", task2.getLabel());
    Assert.assertEquals(1, task2.getOrder().intValue());
    list = assayTypeTaskFieldRepository.findByAssayTypeTaskId(task2.getId());
    Assert.assertEquals(1, list.size());
    taskField = list.get(0);
    Assert.assertEquals("field-a", taskField.getFieldName());
    Assert.assertEquals("Field A", taskField.getDisplayName());
    Assert.assertEquals(CustomEntityFieldType.INTEGER, taskField.getType());
    Assert.assertEquals(0, taskField.getFieldOrder().intValue());
    Assert.assertFalse(taskField.isRequired());
    Assert.assertTrue(taskField.isActive());
    Assert.assertEquals("This is a test", taskField.getDescription());

  }

  @Test
  public void updateAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    assayType.addField(field);
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    Assert.assertFalse(optional.get().getFields().isEmpty());
    Assert.assertEquals(1, optional.get().getFields().size());

    assayType = optional.get();
    field = new AssayTypeField();
    field.setDisplayName("Description");
    field.setFieldName("description");
    field.setRequired(false);
    field.setType(CustomEntityFieldType.TEXT);
    field.setFieldOrder(2);
    assayType.getFields().add(field);
    assayTypeService.update(assayType);

    optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertFalse(optional.get().getFields().isEmpty());
    Assert.assertEquals(2, optional.get().getFields().size());
  }

  @Test
  public void deleteAssayTypeTest() {
    Assert.assertEquals(ASSAY_TYPE_COUNT, assayTypeService.count());
    AssayType assayType = new AssayType();
    assayType.setActive(true);
    assayType.setName("Test");
    assayType.setDescription("This is a test");
    AssayTypeField field = new AssayTypeField();
    field.setDisplayName("Name");
    field.setFieldName("name");
    field.setRequired(true);
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    assayType.setFields(new HashSet<>(Arrays.asList(field)));
    assayTypeService.create(assayType);
    Assert.assertEquals(ASSAY_TYPE_COUNT + 1, assayTypeService.count());

    Optional<AssayType> optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals("Test", optional.get().getName());
    assayType = optional.get();
    assayTypeService.delete(assayType);

    optional = assayTypeService.findByName("Test");
    Assert.assertTrue(optional.isPresent());
    Assert.assertFalse(optional.get().isActive());
  }
}
