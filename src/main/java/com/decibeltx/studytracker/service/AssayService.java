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

package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.eln.NotebookFolder;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.AssayTask;
import com.decibeltx.studytracker.model.AssayTypeField;
import com.decibeltx.studytracker.model.CustomEntityFieldType;
import com.decibeltx.studytracker.model.ELNFolder;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.repository.AssayTaskRepository;
import com.decibeltx.studytracker.storage.StorageFolder;
import com.decibeltx.studytracker.storage.StudyStorageService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayService.class);

  private static final SimpleDateFormat JAVASCRIPT_DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //2021-01-02T05:00:00.000Z

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private AssayTaskRepository assayTaskRepository;

  @Autowired
  private StudyStorageService storageService;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @Autowired
  private NamingService namingService;

  public Optional<Assay> findById(Long id) {
    return assayRepository.findById(id);
  }

  public Optional<Assay> findByCode(String code) {
    return assayRepository.findByCode(code);
  }

  public List<Assay> findByStudyId(Long studyId) {
    return assayRepository.findByStudyId(studyId);
  }

  public List<Assay> findAll() {
    return assayRepository.findAll();
  }

  private boolean isValidFieldType(Object value, CustomEntityFieldType type) {
    Class<?> clazz = value.getClass();
    System.out.println(clazz.getName());
    switch (type) {
      case STRING:
        return String.class.isAssignableFrom(clazz);
      case TEXT:
        return String.class.isAssignableFrom(clazz);
      case DATE:
        if (Date.class.isAssignableFrom(clazz)) {
          System.out.println("Date as Date");
          System.out.println(value.toString());
          return true;
        } else if (String.class.isAssignableFrom(clazz)) {
          System.out.println("Date as String");
          System.out.println(value.toString());
          try {
            JAVASCRIPT_DATE_FORMAT.parse((String) value);
            return true;
          } catch (Exception e) {
            e.printStackTrace();
            return false;
          }
        } else {
          System.out.println("Date as integer");
          System.out.println(value.toString());
          try {
            new Date((long) value);
            return true;
          } catch (Exception e) {
            e.printStackTrace();
            return false;
          }
        }
      case INTEGER:
        return Integer.class.isAssignableFrom(clazz);
      case FLOAT:
        return Double.class.isAssignableFrom(clazz);
      case BOOLEAN:
        return Boolean.class.isAssignableFrom(clazz);
      default:
        return false;
    }
  }

  private void validateAssayFields(Assay assay) {
    for (AssayTypeField assayTypeField : assay.getAssayType().getFields()) {
      if (!assay.getFields().containsKey(assayTypeField.getFieldName())) {
        throw new InvalidConstraintException(
            String.format("Assay %s does not have field %s defined in fields attribute.",
                assay.getName(), assayTypeField.getFieldName()));
      }
      Object value = assay.getFields().get(assayTypeField.getFieldName());
      if (assayTypeField.isRequired() && value == null) {
        throw new InvalidConstraintException(
            String.format("Assay %s does not have required field %s set in fields attribute.",
                assay.getName(), assayTypeField.getFieldName()));
      }
      if (value != null && !isValidFieldType(value, assayTypeField.getType())) {
        throw new InvalidConstraintException(
            String.format(
                "Assay %s field %s does not have the appropriate value set for it's required type "
                    + "%s. Received %s, expected %s",
                assay.getName(),
                assayTypeField.getFieldName(),
                assayTypeField.getType().toString(),
                value.getClass().getName(),
                assayTypeField.getType().toString()
            ));
      }
    }
  }

  @Transactional
  public void create(Assay assay) {

    LOGGER.info("Creating new assay record with name: " + assay.getName());

    validateAssayFields(assay);

    assay.setCode(namingService.generateAssayCode(assay));
    assay.setActive(true);

    for (AssayTask task: assay.getTasks()) {
      task.setAssay(assay);
    }

    // Create the storage folder
    try {
      storageService.createAssayFolder(assay);
      StorageFolder folder = storageService.getAssayFolder(assay);
      assay.setStorageFolder(FileStoreFolder.from(folder));
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Failed to create storage folder for assay: " + assay.getCode());
    }

    // Create the ELN folder
    if (notebookService != null) {
      try {
        NotebookFolder notebookFolder = notebookService.createAssayFolder(assay);
        assay.setNotebookFolder(ELNFolder.from(notebookFolder));
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to create notebook entry for assay: " + assay.getCode());
      }
    }

    assayRepository.save(assay);

  }

  @Transactional
  public Assay update(Assay updated) {

    LOGGER.info("Updating assay record with code: " + updated.getCode());
    Assay assay = assayRepository.getOne(updated.getId());

    assay.setDescription(updated.getDescription());
    assay.setStartDate(updated.getStartDate());
    assay.setEndDate(updated.getEndDate());
    assay.setStatus(updated.getStatus());
    assay.setOwner(updated.getOwner());
    assay.setUsers(updated.getUsers());
    assay.setAttributes(updated.getAttributes());
    assay.setFields(updated.getFields());
//    assay.setTasks(updated.getTasks());

    // Update the tasks
    for (AssayTask task: updated.getTasks()) {
      if (task.getId() != null) {
        AssayTask t = assayTaskRepository.getOne(task.getId());
        t.setStatus(task.getStatus());
        t.setOrder(task.getOrder());
        t.setLabel(task.getLabel());
        assayTaskRepository.save(t);
      } else {
        task.setAssay(assay);
        assay.addTask(task);
      }
    }

    assayRepository.save(assay);

    return assay;

  }

  @Transactional
  public void delete(Assay assay) {
    assay.setActive(false);
    assayRepository.save(assay);
  }

  @Transactional
  public void updateStatus(Assay assay, Status status) {
    assay.setStatus(status);
    if (status.equals(Status.COMPLETE) && assay.getEndDate() == null) {
      assay.setEndDate(new Date());
    }
    assayRepository.save(assay);
  }

  public long count() {
    return assayRepository.count();
  }

  public long countFromDate(Date startDate) {
    return assayRepository.countByCreatedAtAfter(startDate);
  }

  public long countBeforeDate(Date endDate) {
    return assayRepository.countByCreatedAtBefore(endDate);
  }

  public long countBetweenDates(Date startDate, Date endDate) {
    return assayRepository.countByCreatedAtBetween(startDate, endDate);
  }

}
