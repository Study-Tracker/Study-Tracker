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
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayTaskField;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.TaskStatus;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskFieldRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.service.AssayTaskService;
import java.util.Date;
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
public class AssayTaskServiceTests {

  @Autowired private AssayTaskService assayTaskService;

  @Autowired private AssayRepository assayRepository;

  @Autowired private AssayTaskFieldRepository assayTaskFieldRepository;

  @Autowired private ExampleDataRunner exampleDataRunner;
  @Autowired
  private AssayTaskRepository assayTaskRepository;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void findAssayTasks() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
  }

  @Test
  public void addTaskTest() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getCreatedBy();
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());

    AssayTask task = new AssayTask();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    task.setAssay(assay);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    assayTaskService.addAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(2, tasks.size());

    task =
        tasks.stream()
            .filter(t -> t.getLabel().equals("Test task"))
            .findFirst()
            .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(task);
    Assert.assertEquals("Test task", task.getLabel());
    Assert.assertNotNull(task.getCreatedAt());
    Assert.assertNotNull(task.getOrder());
    Assert.assertEquals(1, (int) task.getOrder());
  }

  @Test
  public void addTaskWithFieldsTest() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getCreatedBy();
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());

    AssayTask task = new AssayTask();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    task.setAssay(assay);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    task.setAssignedTo(user);
    task.setDueDate(new Date());

    AssayTaskField field = new AssayTaskField();
    field.setDisplayName("Test field 1");
    field.setFieldName("test_field_1");
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(true);
    task.addField(field);

    field = new AssayTaskField();
    field.setDisplayName("Test field 2");
    field.setFieldName("test_field_2");
    field.setType(CustomEntityFieldType.INTEGER);
    field.setFieldOrder(2);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(false);
    task.addField(field);

    assayTaskService.addAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(2, tasks.size());

    task =
        tasks.stream()
            .filter(t -> t.getLabel().equals("Test task"))
            .findFirst()
            .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(task);
    Assert.assertEquals("Test task", task.getLabel());
    Assert.assertNotNull(task.getCreatedAt());
    Assert.assertNotNull(task.getOrder());
    Assert.assertEquals(1, (int) task.getOrder());
    Assert.assertEquals(user.getId(), task.getAssignedTo().getId());
    Assert.assertNotNull(task.getDueDate());

    List<AssayTaskField> fields = assayTaskFieldRepository.findByAssayTaskId(task.getId());

    Assert.assertEquals(2, fields.size());

    AssayTaskField taskField = fields.stream()
        .filter(f -> f.getFieldName().equals("test_field_1"))
        .findFirst()
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals("Test field 1", taskField.getDisplayName());
    Assert.assertEquals("test_field_1", taskField.getFieldName());
    Assert.assertEquals(CustomEntityFieldType.STRING, taskField.getType());
    Assert.assertEquals(1, (int) taskField.getFieldOrder());
    Assert.assertTrue(taskField.isActive());
    Assert.assertEquals("This is a test", taskField.getDescription());
    Assert.assertTrue(taskField.isRequired());

    taskField = fields.stream()
        .filter(f -> f.getFieldName().equals("test_field_2"))
        .findFirst()
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals("Test field 2", taskField.getDisplayName());
    Assert.assertEquals("test_field_2", taskField.getFieldName());
    Assert.assertEquals(CustomEntityFieldType.INTEGER, taskField.getType());
    Assert.assertEquals(2, (int) taskField.getFieldOrder());
    Assert.assertTrue(taskField.isActive());
    Assert.assertEquals("This is a test", taskField.getDescription());
    Assert.assertFalse(taskField.isRequired());

  }

  @Test
  public void updateTaskTest() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    Date then = assay.getUpdatedAt();
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
    AssayTask task = assayTaskRepository.findById(tasks.get(0).getId()).orElseThrow();
    Assert.assertEquals(TaskStatus.TODO, task.getStatus());

    task.setStatus(TaskStatus.COMPLETE);
    assayTaskService.updateAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
    task = tasks.get(0);
    Assert.assertEquals(TaskStatus.COMPLETE, task.getStatus());
    Assert.assertNotEquals(task.getCreatedAt(), task.getUpdatedAt());

    Assay updatedAssay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    Assert.assertNotEquals(then, updatedAssay.getUpdatedAt());
  }

  @Test
  public void updateTaskStatusTest() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    User user = assay.getCreatedBy();
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());

    AssayTask task = new AssayTask();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    task.setAssay(assay);
    task.setCreatedBy(user);
    task.setLastModifiedBy(user);
    task.setAssignedTo(user);
    task.setDueDate(new Date());

    AssayTaskField field = new AssayTaskField();
    field.setDisplayName("Test field 1");
    field.setFieldName("test_field_1");
    field.setType(CustomEntityFieldType.STRING);
    field.setFieldOrder(1);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(true);
    task.addField(field);

    field = new AssayTaskField();
    field.setDisplayName("Test field 2");
    field.setFieldName("test_field_2");
    field.setType(CustomEntityFieldType.INTEGER);
    field.setFieldOrder(2);
    field.setActive(true);
    field.setDescription("This is a test");
    field.setRequired(false);
    task.addField(field);

    AssayTask created = assayTaskService.addAssayTask(task, assay);
    Assert.assertNotNull(created);
    Assert.assertNotNull(created.getId());
    Assert.assertEquals("Test task", created.getLabel());
    Assert.assertEquals(TaskStatus.TODO, created.getStatus());
    Assert.assertNotNull(created.getCreatedAt());
    Assert.assertNotNull(created.getOrder());
    Assert.assertEquals(1, (int) created.getOrder());
    Assert.assertEquals(user.getId(), created.getAssignedTo().getId());
    Assert.assertNotNull(created.getDueDate());
    Assert.assertEquals(0, created.getData().size());

    created.setStatus(TaskStatus.COMPLETE);
    created.getData().put("test_field_1", "Test value 1");
    created.getData().put("test_field_2", 123);
    assayTaskService.updateAssayTaskStatus(created, created.getStatus(), created.getData());

    AssayTask updated = assayTaskService.findById(created.getId())
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(TaskStatus.COMPLETE, updated.getStatus());
    Assert.assertEquals("Test value 1", updated.getData().get("test_field_1"));
    Assert.assertEquals(123, updated.getData().get("test_field_2"));

  }

  @Test
  public void deleteTaskTest() {
    Assay assay =
        assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    List<AssayTask> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
    AssayTask task = tasks.get(0);

    assayTaskService.deleteAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertTrue(tasks.isEmpty());
  }
}
