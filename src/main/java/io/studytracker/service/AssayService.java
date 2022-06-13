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

package io.studytracker.service;

import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.eln.StudyNotebookService;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.FileStoreFolderRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
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

  private static final SimpleDateFormat JAVASCRIPT_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // 2021-01-02T05:00:00.000Z

  @Autowired private AssayRepository assayRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private AssayTaskRepository assayTaskRepository;

  @Autowired private StudyStorageService storageService;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @Autowired private NamingService namingService;

  @Autowired private FileStoreFolderRepository fileStoreFolderRepository;

  @Autowired private ELNFolderRepository elnFolderRepository;

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
          System.out.println(value);
          return true;
        } else if (String.class.isAssignableFrom(clazz)) {
          System.out.println("Date as String");
          System.out.println(value);
          try {
            JAVASCRIPT_DATE_FORMAT.parse((String) value);
            return true;
          } catch (Exception e) {
            e.printStackTrace();
            return false;
          }
        } else {
          System.out.println("Date as integer");
          System.out.println(value);
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
            String.format(
                "Assay %s does not have field %s defined in fields attribute.",
                assay.getName(), assayTypeField.getFieldName()));
      }
      Object value = assay.getFields().get(assayTypeField.getFieldName());
      if (assayTypeField.isRequired() && value == null) {
        throw new InvalidConstraintException(
            String.format(
                "Assay %s does not have required field %s set in fields attribute.",
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
                assayTypeField.getType().toString()));
      }
    }
  }

  public void create(Assay assay) {
    this.create(assay, null);
  }

  @Transactional
  public void create(Assay assay, NotebookTemplate template) {

    LOGGER.info("Creating new assay record with name: " + assay.getName());

    validateAssayFields(assay);

    assay.setCode(namingService.generateAssayCode(assay));
    assay.setActive(true);

    for (AssayTask task : assay.getTasks()) {
      task.setAssay(assay);
    }

    // Get the study
    Study study =
        studyRepository
            .findById(assay.getStudy().getId())
            .orElseThrow(
                () ->
                    new RecordNotFoundException("Cannot find study: " + assay.getStudy().getId()));

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
      if (study.getNotebookFolder() != null) {
        try {

          LOGGER.info(String.format("Creating ELN entry for assay: %s", assay.getCode()));

          // Create the notebook folder
          NotebookFolder notebookFolder = notebookService.createAssayFolder(assay);
          assay.setNotebookFolder(ELNFolder.from(notebookFolder));

          // Create the notebook entry
          notebookService.createAssayNotebookEntry(assay, template);

        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.warn("Failed to create notebook entry for assay: " + assay.getCode());
        }
      } else {
        LOGGER.warn(String.format("Assay study %s does not have ELN folder set.", study.getCode()));
      }
    }

    assayRepository.save(assay);
  }

  @Transactional
  public Assay update(Assay updated) {

    LOGGER.info("Updating assay record with code: " + updated.getCode());
    Assay assay = assayRepository.getById(updated.getId());

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
    for (AssayTask task : updated.getTasks()) {
      if (task.getId() != null) {
        AssayTask t = assayTaskRepository.getById(task.getId());
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

  @Transactional
  public void repairStorageFolder(Assay assay) {

    // Find or create the storage folder
    StorageFolder folder;
    try {
      folder = storageService.getAssayFolder(assay, false);
    } catch (StudyStorageNotFoundException e) {
      LOGGER.warn("Storage folder not found for assay: " + assay.getCode());
      try {
        folder = storageService.createAssayFolder(assay);
      } catch (Exception ex) {
        throw new StudyTrackerException(ex);
      }
    }
    LOGGER.debug(folder.toString());

    // Check if a folder record exists in the database
    List<FileStoreFolder> folders = fileStoreFolderRepository.findByPath(folder.getPath());
    FileStoreFolder dbFolder = null;
    if (!folders.isEmpty()) {
      dbFolder = folders.get(0);
    }

    // Assay has no folder record associated
    if (assay.getStorageFolder() == null) {
      if (dbFolder != null) {
        LOGGER.info("Repairing assay folder missing association.");
        assay.setStorageFolder(dbFolder);
      } else {
        LOGGER.info("Repairing assay folder with no record.");
        assay.setStorageFolder(FileStoreFolder.from(folder));
      }
      assayRepository.save(assay);
    }

    // Assay does have a folder record, but it is malformed
    else {
      LOGGER.info("Repairing malformed assay folder record.");
      FileStoreFolder f = fileStoreFolderRepository.getById(assay.getStorageFolder().getId());
      f.setName(folder.getName());
      f.setPath(folder.getPath());
      f.setUrl(folder.getUrl());
      fileStoreFolderRepository.save(f);
    }

  }

  @Transactional
  public void repairElnFolder(Assay assay) {

    // Check to see if the folder exists and create a new one if necessary
    NotebookFolder folder;
    Optional<NotebookFolder> optional = notebookService.findAssayFolder(assay);
    if (optional.isPresent()) {
      folder = optional.get();
    } else {
      folder = notebookService.createAssayFolder(assay);
    }

    // Update the record
    ELNFolder f;
    boolean isNew = false;
    try {
      f = elnFolderRepository.getById(assay.getNotebookFolder().getId());
    } catch (NullPointerException e) {
      f = new ELNFolder();
      isNew = true;
    }
    f.setName(folder.getName());
    f.setPath(folder.getPath());
    f.setUrl(folder.getUrl());
    f.setReferenceId(folder.getReferenceId());
    elnFolderRepository.save(f);

    if (isNew) {
      Assay a = assayRepository.getById(assay.getId());
      a.setNotebookFolder(f);
      assayRepository.save(a);
    }
  }
}
