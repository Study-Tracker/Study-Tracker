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
import io.studytracker.aws.S3StudyStorageService;
import io.studytracker.aws.S3Utils;
import io.studytracker.benchling.BenchlingNotebookEntryService;
import io.studytracker.benchling.BenchlingNotebookFolderService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.git.GitService;
import io.studytracker.git.GitServiceLookup;
import io.studytracker.model.*;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.AssayTaskRepository;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.*;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolationException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

  @Autowired private BenchlingNotebookFolderService notebookFolderService;

  @Autowired private BenchlingNotebookEntryService notebookEntryService;

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

  private StorageDriveFolder createAssayStorageFolder(Assay assay, StorageDriveFolder parentFolder) {
    try {
      StorageDrive drive = storageDriveFolderService.findDriveById(parentFolder.getStorageDrive().getId())
          .orElseThrow(() -> new StudyStorageException("No storage drive found for id: "
              + parentFolder.getStorageDrive().getId()));
      StudyStorageService storageService = storageServiceLookup.lookup(drive.getDriveType())
          .orElseThrow(() -> new StudyStorageNotFoundException("No storage service found for drive type: "
              + parentFolder.getStorageDrive().getDriveType()));
      return storageService.createAssayFolder(parentFolder, assay);
    } catch (Exception e) {
      LOGGER.warn("Failed to create storage folder for assay: " + assay.getCode(), e);
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
    StorageDriveFolder folder = createAssayStorageFolder(assay, parentFolder);
    assay.addStorageFolder(folder, true);

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
            StorageFile movedFile = storageService.saveFile(folder, folder.getPath(), new File(localPath));
            assay.getFields().put(field.getFieldName(), objectMapper.writeValueAsString(movedFile));
        }
      }
    } catch (Exception e) {
      LOGGER.warn("Failed to move file for assay: " + assay.getCode(), e);
    }

    // Create the ELN folder
    if (options.isUseNotebook()) {
      
      ELNFolder assayFolder = null;
      
      // An existing folder was provided
      if (options.getNotebookFolder() != null && StringUtils.hasText(options.getNotebookFolder().getReferenceId())) {
        assayFolder = notebookFolderService.findFolderById(options.getNotebookFolder().getReferenceId());
        assayFolder = elnFolderRepository.save(assayFolder);
      }
      
      // Create a new folder
      else {
        ELNFolder studyFolder = study.getNotebookFolders().stream()
                .filter(f -> f.isPrimary())
                .map(f -> f.getElnFolder())
                .findFirst()
                .orElse(null);
        if (studyFolder != null) {
          assayFolder = notebookFolderService.createAssayFolder(assay);
          elnFolderRepository.save(assayFolder);
        } else {
          LOGGER.warn("Assay study {} does not have ELN folder set.", study.getCode());
        }
      }
      
      if (assayFolder != null) {
        assay.addNotebookFolder(assayFolder, true);
        
        // Create notebook entry
        try {
          LOGGER.info(String.format("Creating ELN entry for assay: %s", assay.getCode()));
          NotebookTemplate template = null;
          if (options.getNotebookTemplateId() != null) {
            Optional<NotebookTemplate> templateOptional =
                    notebookEntryService.findEntryTemplateById(options.getNotebookTemplateId());
            if (templateOptional.isPresent()) {
              template = templateOptional.get();
            } else {
              LOGGER.warn("Cannot find notebook template with id: {}", options.getNotebookTemplateId());
            }
          }
          notebookEntryService.createAssayNotebookEntry(assay, assayFolder, template);
        } catch (Exception e) {
          LOGGER.warn("Failed to create notebook entry for assay: {}", assay.getCode(), e);
        }
        
      }
      
    }

    Assay created;
    try {
      created = assayRepository.save(assay);
      LOGGER.info(
          String.format(
              "Successfully created new assay with code %s and ID %s",
              assay.getCode(), assay.getId()));
    } catch (ConstraintViolationException e) {
      e.printStackTrace();
      throw new InvalidConstraintException(e);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Git repository
    if (options.isUseGit() && options.getGitGroup() != null) {
      LOGGER.info(String.format("Creating git repository for assay: %s", created.getCode()));
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

    // S3
    if (options.isUseS3() && options.getS3FolderId() != null) {
      addS3BucketFolder(assay, study, options);
    }

    return assayRepository.findById(created.getId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find assay: " + created.getId()));

  }

  private void addS3BucketFolder(Assay assay, Study study, AssayOptions options) {
    LOGGER.debug("Creating S3 folder for assay: " + assay.getCode());
    try {

      // Get the requested root folder & drive
      StorageDriveFolder s3RootFolder = storageDriveFolderService.findById(options.getS3FolderId())
          .orElseThrow(() -> new StudyStorageException("Invalid S3 folder ID: "
              + options.getS3FolderId()));
      if (!s3RootFolder.isStudyRoot()) {
        throw new StudyStorageException("S3 folder is not a study root folder: "
            + s3RootFolder.getName());
      }
      StorageDrive s3Drive = storageDriveFolderService.findDriveByFolder(s3RootFolder)
          .orElseThrow(() -> new StudyStorageException("Invalid S3 folder ID: "
              + options.getS3FolderId()));

      // Get the storage service
      S3StudyStorageService s3Service = (S3StudyStorageService) storageDriveFolderService
          .lookupStudyStorageService(s3RootFolder);

      // Make sure the study folder exists. If not, create it.
      StorageDriveFolder studyS3Folder;
      Optional<StorageDriveFolder> optional = storageDriveFolderService.findByStudy(study).stream()
          .filter(f -> f.getStorageDrive().getId().equals(s3Drive.getId()))
          .findFirst();
      if (optional.isPresent()) {
        studyS3Folder = optional.get();
      } else {
        String studyFolderPath = S3Utils.joinS3Path(s3RootFolder.getPath(), S3Utils.generateStudyFolderName(study));
        StorageDriveFolder studyFolder = new StorageDriveFolder();
        studyFolder.setPath(studyFolderPath);
        studyFolder.setName("Study " + study.getCode() + " S3 Folder");
        studyFolder.setStorageDrive(s3Drive);
        studyFolder.setWriteEnabled(true);
        studyFolder.setDetails(new S3FolderDetails());
        studyS3Folder = storageDriveFolderService.registerFolder(studyFolder, s3Drive);
        study.addStorageFolder(studyS3Folder);
        studyRepository.save(study);
      }

      // Create the study S3 folder
      StorageDriveFolder assayS3Folder = s3Service.createStudyFolder(studyS3Folder, study);
      assay.addStorageFolder(assayS3Folder);
      assayRepository.save(assay);
    } catch (StudyStorageException e) {
      e.printStackTrace();
      LOGGER.error("Failed to create S3 folder for assay: " + assay.getCode(), e);
    }
  }

  @Transactional
  public Assay update(Assay updated) {

    LOGGER.info("Updating assay record with code: " + updated.getCode());
    Assay assay = assayRepository.getById(updated.getId());

    assay.setName(updated.getName());
    assay.setDescription(updated.getDescription());
    assay.setStartDate(updated.getStartDate());
    assay.setEndDate(updated.getEndDate());
    assay.setStatus(updated.getStatus());
    assay.setOwner(updated.getOwner());
    assay.setUsers(updated.getUsers());
    assay.setAttributes(updated.getAttributes());
    assay.setFields(updated.getFields());

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
  public void addStorageFolder(Assay assay, StorageDriveFolder folder) {
    Assay a = assayRepository.getById(assay.getId());
    a.addStorageFolder(folder);
    assayRepository.save(a);
  }

  @Transactional
  public void delete(Assay assay) {
    assay.setActive(false);
    assayRepository.save(assay);
  }

  @Transactional
  public void restore(Assay assay) {
    assay.setActive(true);
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
      StorageDriveFolder assayStorageFolder = this.createAssayStorageFolder(assay, parentFolder);
      assay.addStorageFolder(assayStorageFolder, true);
      assayRepository.save(assay);
      LOGGER.info("Created primary storage folder for assay: " + assay.getCode()
          + " at path: " + assayStorageFolder.getPath());
    }

  }

  @Transactional
  public void repairElnFolder(Assay assay) {

    // Check to see if the folder exists and create a new one if necessary
    ELNFolder folder;
    Optional<ELNFolder> optional = notebookFolderService.findPrimaryAssayFolder(assay);
    folder = optional.orElseGet(() -> notebookFolderService.createAssayFolder(assay));

    // Update the record
    ELNFolder f;
    boolean isNew = false;
    try {
      AssayNotebookFolder anf = assay.getNotebookFolders().stream()
          .filter(AssayNotebookFolder::isPrimary)
          .findFirst().orElse(null);
      f = elnFolderRepository.getById(anf.getElnFolder().getId());
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
      a.addNotebookFolder(f, true);
      assayRepository.save(a);
    }
  }
  
  public List<Assay> search(String keyword) {
    return assayRepository.findByNameOrCodeLike(keyword);
  }
  
  @Transactional
  public void moveAssayToStudy(Assay assay, Study study) {
    
    LOGGER.info("Attempting to move assay {} to study {}", assay.getCode(), study.getCode());
    
    // Update the assay record
    Assay a = assayRepository.getById(assay.getId());
    a.setStudy(study);
    a.setCode(namingService.generateAssayCode(a));
    
    // Create a new primary storage folder
    StorageDriveFolder parentFolder = this.storageDriveFolderService.findPrimaryStudyFolder(study).orElse(null);
    if (parentFolder != null) {
      StorageDriveFolder storageFolder = this.createAssayStorageFolder(a, parentFolder);
      a.addStorageFolder(storageFolder, true);
    } else {
      LOGGER.warn("No primary storage folder found for study {}. No new study storage folder will be created. ", study.getCode());
    }
    
    // Create a new ELN folder
    ELNFolder studyElnFolder = elnFolderRepository.findPrimaryByStudyId(study.getId()).orElse(null);
    if (studyElnFolder != null) {
      ELNFolder assayElnFolder = notebookFolderService.createAssayFolder(a);
      elnFolderRepository.save(assayElnFolder);
      a.addNotebookFolder(assayElnFolder, true);
    } else {
      LOGGER.warn("No primary ELN folder found for study {}. No new study ELN folder will be created. ", study.getCode());
      
      assayRepository.save(a);
      LOGGER.info("Successfully moved assay with new code {} to study {}", a.getCode(), study.getCode());
      
    }
    
  }
  
}
