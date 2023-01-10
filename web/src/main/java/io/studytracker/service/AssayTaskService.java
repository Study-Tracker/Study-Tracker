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

package io.studytracker.service;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayTaskField;
import io.studytracker.model.TaskStatus;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskFieldRepository;
import io.studytracker.repository.AssayTaskRepository;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssayTaskService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTaskService.class);

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTaskRepository assayTaskRepository;

  @Autowired
  private AssayTaskFieldRepository assayTaskFieldRepository;

  public Optional<AssayTask> findById(Long id) {
    return assayTaskRepository.findById(id);
  }

  public Page<AssayTask> findAll(Pageable pageable) {
    return assayTaskRepository.findAll(pageable);
  }

  public Page<AssayTask> findAssayTasks(Long id, Pageable pageable) {
    return assayTaskRepository.findByAssayId(id, pageable);
  }

  public Page<AssayTask> findAssayTasks(Assay assay, Pageable pageable) {
    return this.findAssayTasks(assay.getId(), pageable);
  }

  public List<AssayTask> findAssayTasks(Assay assay) {
    return this.findAssayTasks(assay.getId());
  }

  public List<AssayTask> findAssayTasks(Long id) {
    return assayTaskRepository.findByAssayId(id);
  }

  @Transactional
  public AssayTask addAssayTask(AssayTask task, Assay assay) {
    LOGGER.info("Adding new task to assay {}: {}", assay.getCode(), task);
    if (task.getOrder() == null) {
      task.setOrder(assay.getTasks().size());
    }
    task.setAssay(assay);
    for (AssayTaskField field: task.getFields()) {
      field.setAssayTask(task);
    }
    AssayTask created = assayTaskRepository.save(task);
    return assayTaskRepository.findById(created.getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay task: " + created.getId()));
  }

  @Transactional
  public AssayTask updateAssayTask(AssayTask task, Assay assay) {
    AssayTask t = assayTaskRepository.getById(task.getId());
    t.setAssay(assay);
    t.setStatus(task.getStatus());
    t.setOrder(task.getOrder());
    t.setLabel(task.getLabel());
    t.setDueDate(task.getDueDate());
    t.setAssignedTo(task.getAssignedTo());
    t.getFields().clear();
    t.addFields(task.getFields());
    t.setData(task.getData());
    assayTaskRepository.save(t);
    Assay a = assayRepository.getById(assay.getId());
    a.setUpdatedAt(new Date());
    assayRepository.save(a);
    return assayTaskRepository.findById(task.getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay task: " + task.getId()));
  }

  @Transactional
  public void updateAssayTaskStatus(AssayTask task, TaskStatus status, Map<String, Object> data) {
    AssayTask t = assayTaskRepository.getById(task.getId());
    t.setStatus(status);
    if (data != null) {
      t.setData(data);
    }
    assayTaskRepository.save(t);
  }

  public void updateAssayTaskStatus(AssayTask task, TaskStatus status) {
    this.updateAssayTaskStatus(task, status, null);
  }

  @Transactional
  public void deleteAssayTask(AssayTask task, Assay assay) {
    assay.removeTask(task.getId());
    assayRepository.save(assay);
  }
}
