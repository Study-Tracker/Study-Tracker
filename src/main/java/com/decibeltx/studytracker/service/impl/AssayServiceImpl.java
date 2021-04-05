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

package com.decibeltx.studytracker.service.impl;

import com.decibeltx.studytracker.eln.NotebookFolder;
import com.decibeltx.studytracker.eln.StudyNotebookService;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.AssayTypeField;
import com.decibeltx.studytracker.model.AssayTypeField.AssayFieldType;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.AssayRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.service.AssayService;
import com.decibeltx.studytracker.service.NamingService;
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

@Service
public class AssayServiceImpl implements AssayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayServiceImpl.class);

  private static final SimpleDateFormat JAVASCRIPT_DATE_FORMAT = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); //2021-01-02T05:00:00.000Z

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyStorageService storageService;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @Autowired
  private NamingService namingService;

  @Override
  public Optional<Assay> findById(String id) {
    return assayRepository.findById(id);
  }

  @Override
  public Optional<Assay> findByCode(String code) {
    return assayRepository.findByCode(code);
  }

  @Override
  public List<Assay> findByStudyId(String studyId) {
    return assayRepository.findByStudyId(studyId);
  }

  @Override
  public List<Assay> findAll() {
    return assayRepository.findAll();
  }

  private boolean isValidFieldType(Object value, AssayFieldType type) {
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

  @Override
  public void create(Assay assay) {

    LOGGER.info("Creating new assay record with name: " + assay.getName());
    Study study = studyRepository.findById(assay.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);

    validateAssayFields(assay);
    assay.setCode(namingService.generateAssayCode(assay));
    assay.setActive(true);

    assayRepository.insert(assay);

    study.getAssays().add(assay);
    studyRepository.save(study);

    // Create the storage folder
    try {
      storageService.createAssayFolder(assay);
      StorageFolder folder = storageService.getAssayFolder(assay);
      assay.setStorageFolder(folder);
      assay.setUpdatedAt(new Date());
      assayRepository.save(assay);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Failed to create storage folder for assay: " + assay.getCode());
    }

    // Create the ELN folder
    if (notebookService != null) {
      try {
        NotebookFolder notebookFolder = notebookService.createAssayFolder(assay);
        assay.setNotebookFolder(notebookFolder);
        assay.setUpdatedAt(new Date());
        assayRepository.save(assay);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.error("Failed to create notebook entry for assay: " + assay.getCode());
      }
    }

  }

  @Override
  public void update(Assay updated) {
    LOGGER.info("Updating assay record with code: " + updated.getCode());
    Assay assay = assayRepository.findById(updated.getId())
        .orElseThrow(RecordNotFoundException::new);

    assay.setDescription(updated.getDescription());
    assay.setStartDate(updated.getStartDate());
    assay.setEndDate(updated.getEndDate());
    assay.setStatus(updated.getStatus());
    assay.setOwner(updated.getOwner());
    assay.setUsers(updated.getUsers());
    assay.setAttributes(updated.getAttributes());
    assay.setFields(updated.getFields());
    assay.setTasks(updated.getTasks());

    assayRepository.save(assay);
  }

  @Override
  public void delete(Assay assay) {
    assay.setActive(false);
    assayRepository.save(assay);
  }

  @Override
  public void updateStatus(Assay assay, Status status) {
    assay.setStatus(status);
    if (status.equals(Status.COMPLETE) && assay.getEndDate() == null) {
      assay.setEndDate(new Date());
    }
    assayRepository.save(assay);
  }

  @Override
  public long count() {
    return assayRepository.count();
  }

  @Override
  public long countFromDate(Date startDate) {
    return assayRepository.countByCreatedAtAfter(startDate);
  }

  @Override
  public long countBeforeDate(Date endDate) {
    return assayRepository.countByCreatedAtBefore(endDate);
  }

  @Override
  public long countBetweenDates(Date startDate, Date endDate) {
    return assayRepository.countByCreatedAtBetween(startDate, endDate);
  }
}
