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

package com.decibeltx.studytracker.test.core.service;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Task;
import com.decibeltx.studytracker.model.Task.TaskStatus;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.service.AssayTaskService;
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

  @Autowired
  private AssayTaskService assayTaskService;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private ExampleDataGenerator exampleDataGenerator;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void findAssayTasks() {
    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    List<Task> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
  }

  @Test
  public void addTaskTest() {
    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    List<Task> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());

    Task task = new Task();
    task.setStatus(TaskStatus.TODO);
    task.setLabel("Test task");
    assayTaskService.addAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(2, tasks.size());

    task = tasks.stream()
        .filter(t -> t.getLabel().equals("Test task"))
        .findFirst().orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(task);
    Assert.assertEquals("Test task", task.getLabel());
    Assert.assertNotNull(task.getCreatedAt());
    Assert.assertNotNull(task.getOrder());
    Assert.assertEquals(1, (int) task.getOrder());

  }

  @Test
  public void updateTaskTest() {
    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    List<Task> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
    Task task = tasks.get(0);
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
  }

  @Test
  public void deleteTaskTest() {
    Assay assay = assayRepository.findByCode("PPB-10001-001")
        .orElseThrow(RecordNotFoundException::new);
    List<Task> tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertFalse(tasks.isEmpty());
    Assert.assertEquals(1, tasks.size());
    Task task = tasks.get(0);

    assayTaskService.deleteAssayTask(task, assay);

    tasks = assayTaskService.findAssayTasks(assay);
    Assert.assertNotNull(tasks);
    Assert.assertTrue(tasks.isEmpty());
  }

}
