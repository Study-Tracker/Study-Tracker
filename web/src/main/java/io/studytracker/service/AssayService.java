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

package io.studytracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.git.GitService;
import io.studytracker.git.GitServiceLookup;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayOptions;
import io.studytracker.model.AssayStorageFolder;
import io.studytracker.model.AssayTask;
import io.studytracker.model.AssayTaskField;
import io.studytracker.model.AssayTypeField;
import io.studytracker.model.CustomEntityFieldType;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitRepository;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFile;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StorageUtils;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  @Autowired private StudyStorageServiceLookup storageServiceLookup;

  @Autowired private StorageDriveFolderService storageDriveFolderService;

  @Autowired(required = false)
  private NotebookFolderService notebookFolderService;

  @Autowired(required = false)
  private NotebookEntryService notebookEntryService;

  @Autowired private NamingService namingService;

  @Autowired private ELNFolderRepository elnFolderRepository;

  @Autowired private GitServiceLookup gitServiceLookup;

  @Autowired private ObjectMapper objectMapper;

  public Page<Assay> findAll(Pageable pageable) {
    return assayRepository.findAll(pageable);
  }

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
      case DROPDOWN:
        return String.class.isAssignableFrom(clazz);
      case FILE:
        return String.class.isAssignableFrom(clazz);
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

  private AssayStorageFolder createAssayStorageFolder(Assay assay, StorageDriveFolder parentFolder) {
    try {
      StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
          .orElseThrow(() -> new StudyStorageException("No storage drive found for id: "
              + parentFolder.getStorageDrive().getId()));
      StudyStorageService storageService = storageServiceLookup.lookup(drive.getDriveType())
          .orElseThrow(() -> new StudyStorageNotFoundException("No storage service found for drive type: "
              + parentFolder.getStorageDrive().getDriveType()));
      StorageDriveFolder folder = storageService.createAssayFolder(parentFolder, assay);
      AssayStorageFolder assayStorageFolder = new AssayStorageFolder();
      assayStorageFolder.setStorageDriveFolder(folder);
      assayStorageFolder.setAssay(assay);
      return assayStorageFolder;
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Failed to create storage folder for assay: " + assay.getCode());
      throw new StudyTrackerException(e);
    }
  }

  public void create(Assay assay) {
    this.create(assay, new AssayOptions());
  }

  @Transactional
  public Assay create(Assay assay, AssayOptions options) {

    LOGGER.info("Creating new assay record with name: " + assay.getName());

    validateAssayFields(assay);

    assay.setCode(namingService.generateAssayCode(assay));
    assay.setActive(true);

    for (AssayTask task : assay.getTasks()) {
      for (AssayTaskField field : task.getFields()) {
        field.setAssayTask(task);
      }
      task.setAssay(assay);
    }

    // Get the study
    Study study =
        studyRepository
            .findById(assay.getStudy().getId())
            .orElseThrow(
                () ->
                    new RecordNotFoundException("Cannot find study: " + assay.getStudy().getId()));

    // Get the parent storage folder
    Long parentFolderId;
    if (options.getParentFolder() == null) {
      parentFolderId = study.getStorageFolders().stream()
          .filter(f -> f.isPrimary())
          .findFirst()
          .orElseThrow(() -> new RecordNotFoundException("No primary storage folder found for program: "
              + study.getName()))
          .getStorageDriveFolder()
          .getId();
    } else {
      parentFolderId = options.getParentFolder().getId();
    }
    final StorageDriveFolder parentFolder = storageDriveFolderService.findById(parentFolderId)
        .orElseThrow(() -> new RecordNotFoundException("No storage folder found for id: "
            + parentFolderId));


    // Create default storage folder
    AssayStorageFolder folder = createAssayStorageFolder(assay, parentFolder);
    folder.setPrimary(true);
    assay.addStorageFolder(folder);

    // Handle attached files
    try {
      StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
          .orElseThrow(() -> new StudyStorageException("No storage drive found for id: "
              + parentFolder.getStorageDrive().getId()));
      StudyStorageService storageService = storageServiceLookup.lookup(drive.getDriveType())
          .orElseThrow(() -> new StudyStorageNotFoundException("No storage service found for drive type: "
              + parentFolder.getStorageDrive().getDriveType()));

      // Move uploaded files to new folder
      for (AssayTypeField field : assay.getAssayType().getFields()) {
        if (field.getType().equals(CustomEntityFieldType.FILE)
            && assay.getFields().containsKey(field.getFieldName())
            && assay.getFields().get(field.getFieldName()) != null) {
            String localPath = StorageUtils.cleanInputPath(
                assay.getFields().get(field.getFieldName()).toString());
            StorageFile movedFile = storageService
                .saveFile(folder.getStorageDriveFolder(), folder.getStorageDriveFolder().getPath(),
                    new File(localPath));
            assay.getFields().put(field.getFieldName(), objectMapper.writeValueAsString(movedFile));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Failed to move file for assay: " + assay.getCode());
    }

    // Create the ELN folder
    if (options.isUseNotebook() && notebookFolderService != null) {
      if (study.getNotebookFolder() != null) {
        try {

          LOGGER.info(String.format("Creating ELN entry for assay: %s", assay.getCode()));

          // Create the notebook folder
          NotebookFolder notebookFolder = notebookFolderService.createAssayFolder(assay);
          assay.setNotebookFolder(ELNFolder.from(notebookFolder));

          // Create the notebook entry
          NotebookTemplate template = null;
          if (options.getNotebookTemplateId() != null) {
            Optional<NotebookTemplate> templateOptional =
                notebookEntryService.findEntryTemplateById(options.getNotebookTemplateId());
            if (templateOptional.isPresent()) {
              template = templateOptional.get();
            } else {
              LOGGER.warn("Cannot find notebook template with id: " + options.getNotebookTemplateId());
            }
          }
          notebookEntryService.createAssayNotebookEntry(assay, template);

        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.warn("Failed to create notebook entry for assay: " + assay.getCode());
        }
      } else {
        LOGGER.warn(String.format("Assay study %s does not have ELN folder set.", study.getCode()));
      }
    } else {
      assay.setNotebookFolder(null);
    }

    Assay created;
    try {
      created = assayRepository.save(assay);
      LOGGER.info(
          String.format(
              "Successfully created new assay with code %s and ID %s",
              assay.getCode(), assay.getId()));
    } catch (ConstraintViolationException e) {
      throw new InvalidConstraintException(e);
    } catch (Exception e) {
      throw e;
    }

    // Git repository
    if (options.isUseGit() && options.getGitGroup() != null) {
      try {
        GitGroup parentGroup = options.getGitGroup();
        GitService gitService = gitServiceLookup.lookup(parentGroup.getGitServiceType())
            .orElseThrow(() -> new InvalidRequestException(
                "Git service not found: " + parentGroup.getGitServiceType()));
        GitRepository repository = gitService.createAssayRepository(parentGroup, created);
        created.addGitRepository(repository);
        assayRepository.save(created);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to create git repository for assay: " + created.getCode());
      }
    }

    return created;

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

    LOGGER.info("Attempting to repair primary storage folder for assay: " + assay.getCode());

    StorageDrive drive;
    StorageDriveFolder folder;
    Optional<StorageDriveFolder> optional = storageDriveFolderService.findPrimaryAssayFolder(assay);

    // If the study already has a primary storage folder record...
    if (optional.isPresent()) {
      folder = optional.get();
      drive = storageDriveFolderService.findDriveById(folder.getStorageDrive().getId())
          .orElseThrow(() -> new RecordNotFoundException("Could not find storage drive with ID: "
              + folder.getStorageDrive().getId()));
      StudyStorageService storageService = storageDriveFolderService
          .lookupStudyStorageService(folder.getStorageDrive().getDriveType());

      // If the folder exists, do nothing
      if (storageService.folderExists(drive, folder.getPath())) {
        LOGGER.warn("Primary storage folder for assay: " + assay.getCode()
            + " already exists and is valid. No action taken.");
        return;
      }

      // If no, create the folder for the existing registered path
      else {
        String folderPath = StorageUtils.getParentPathFromPath(folder.getPath());
        try {
          StorageFolder storageFolder = storageService.createFolder(drive, folderPath, folder.getName());
          LOGGER.info("Created primary storage folder for assay: " + assay.getCode()
              + " at path: " + storageFolder.getPath());
        } catch (Exception e) {
          e.printStackTrace();
          throw new StudyTrackerException("Could not create storage folder for assay: "
              + assay.getCode() + " at path: " + folderPath, e);
        }
      }

    }

    // If no primary folder record exists, create one
    else {

      Study study = studyRepository.findById(assay.getStudy().getId())
          .orElseThrow(() -> new RecordNotFoundException("Could not find assay with ID: "
              + assay.getStudy().getId()));
      StorageDriveFolder parentFolder = storageDriveFolderService
          .findPrimaryStudyFolder(study)
          .orElseThrow(() -> new RecordNotFoundException("Could not find primary study folder : "
              + study.getCode()));
      AssayStorageFolder assayStorageFolder = this.createAssayStorageFolder(assay, parentFolder);
      assayStorageFolder.setPrimary(true);
      assay.addStorageFolder(assayStorageFolder);
      assayRepository.save(assay);
      LOGGER.info("Created primary storage folder for assay: " + assay.getCode()
          + " at path: " + assayStorageFolder.getStorageDriveFolder().getPath());
    }

  }

  @Transactional
  public void repairElnFolder(Assay assay) {

    // Check to see if the folder exists and create a new one if necessary
    NotebookFolder folder;
    Optional<NotebookFolder> optional = notebookFolderService.findAssayFolder(assay);
    folder = optional.orElseGet(() -> notebookFolderService.createAssayFolder(assay));

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
