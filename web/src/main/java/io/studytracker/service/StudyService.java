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

import io.studytracker.eln.NotebookEntry;
import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookFolder;
import io.studytracker.eln.NotebookFolderService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.exception.DuplicateRecordException;
import io.studytracker.exception.InvalidConstraintException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.git.GitRepository;
import io.studytracker.git.GitService;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyOptionAttributes;
import io.studytracker.model.StudyOptions;
import io.studytracker.model.StudyStorageFolder;
import io.studytracker.model.User;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.StudyStorageServiceLookup;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.net.URL;
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
import org.springframework.util.StringUtils;

/** Service class for reading and writing {@link Study} records. */
@Service
public class StudyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyService.class);

  private StudyRepository studyRepository;

  private ProgramRepository programRepository;

  private NotebookEntryService notebookEntryService;

  private NotebookFolderService notebookFolderService;

  private NamingService namingService;

  private ELNFolderRepository elnFolderRepository;

  private GitService gitService;

  private StudyStorageServiceLookup storageServiceLookup;

  private StorageDriveFolderService storageDriveFolderService;

  /**
   * Finds a single study, identified by its primary key ID
   *
   * @param id pkid
   * @return optional study
   */
  public Optional<Study> findById(Long id) {
    return studyRepository.findById(id);
  }

  /**
   * Returns all study records.
   *
   * @return all studies
   */
  public List<Study> findAll() {
    return studyRepository.findAll();
  }

  /**
   * Returns a {@link Page} of studies
   *
   * @param pageable page request
   * @return page of studies
   */
  public Page<Study> findAll(Pageable pageable) {
    return studyRepository.findAll(pageable);
  }

  /**
   * Finds all studies associated with a given {@link Program}
   *
   * @param program program object
   * @return list of studies
   */
  public List<Study> findByProgram(Program program) {
    return studyRepository.findByProgramId(program.getId());
  }

  public List<Study> findByUser(User user) {
    return studyRepository.findByUsersId(user.getId());
  }

  /**
   * Finds a study with the unique given name.
   *
   * @param name study name, unique
   * @return optional study
   */
  public List<Study> findByName(String name) {
    return studyRepository.findByName(name);
  }

  /**
   * Finds a study by its unique internal code.
   *
   * @param code internal code
   * @return optional study
   */
  public Optional<Study> findByCode(String code) {
    return studyRepository.findByCode(code);
  }

  /**
   * Creates a storage folder for the study and returns a {@link StorageFolder} record.
   *
   * @param study study object
   * @return storage folder record
   */
  private StudyStorageFolder createStudyStorageFolder(Study study, StorageDriveFolder parentFolder) {
    try {
      StorageDrive drive = storageDriveFolderService.findDriveByFolder(parentFolder)
          .orElseThrow(() -> new StudyStorageException("No storage drive found for id: "
              + parentFolder.getStorageDrive().getId()));
      StudyStorageService storageService = storageServiceLookup.lookup(drive.getDriveType())
          .orElseThrow(() -> new StudyStorageNotFoundException("No storage service found for drive type: "
              + parentFolder.getStorageDrive().getDriveType()));
      StorageDriveFolder folder = storageService.createStudyFolder(parentFolder, study);
      StudyStorageFolder studyFolder = new StudyStorageFolder();
      studyFolder.setStorageDriveFolder(folder);
      studyFolder.setStudy(study);
      return studyFolder;
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Failed to create storage folder for study: " + study.getCode());
      throw new StudyTrackerException(e);
    }
  }

  /**
   * Creates a folder in the ELN, if necessary, and then saves a {@link ELNFolder} record
   *   associated with the provided {@code study}. If the study is legacy, no new folder is created.
   *
   * @param study study object
   * @param program program object
   * @return ELNFolder record
   */
  private ELNFolder createStudyElnFolder(Study study, Program program) {
    ELNFolder elnFolder = null;
    if (study.isLegacy()) {
      LOGGER.info(String.format("Legacy Study : %s", study.getCode()));
      if (study.getNotebookFolder().getUrl() != null) {
        elnFolder = study.getNotebookFolder();
        elnFolder.setName(NamingService.getStudyNotebookFolderName(study));
      } else {
        LOGGER.warn("No ELN URL set, so folder reference will not be created.");
      }
    } else {
      // New study and notebook integration active
      LOGGER.info(String.format("Creating ELN entry for study: %s", study.getCode()));
      if (program.getNotebookFolder() != null) {
        try {

          // Create the notebook folder
          NotebookFolder notebookFolder = notebookFolderService.createStudyFolder(study);
          elnFolder = ELNFolder.from(notebookFolder);

        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.warn("Failed to create notebook folder and entry for study: " + study.getCode());
        }
      } else {
        LOGGER.warn(
            String.format("Study program %s does not have ELN folder set.", program.getName()));
      }
    }
    return elnFolder;
  }

  public void create(Study study) {
    this.create(study, new StudyOptions());
  }

  /**
   * Creates a new study record, creates a storage folder, creates and ELN folder, and creates an
   * ELN entry for the study.
   *
   * @param study new study
   */
  @Transactional
  public void create(Study study, StudyOptions options) {

    LOGGER.info("Attempting to create new study with name: {}  and options: {}" + study.getName(), options);

    StudyOptionAttributes.setStudyOptionAttributes(study, options);

    // Check for existing studies
    if (study.getCode() != null) {
      Optional<Study> optional = studyRepository.findByCode(study.getCode());
      if (optional.isPresent()) {
        throw new DuplicateRecordException("Duplicate study code: " + study.getCode());
      }
    }
    if (studyRepository.findByName(study.getName()).size() > 0) {
      throw new DuplicateRecordException("Duplicate study name: " + study.getName());
    }

    // Assign the code, if necessary
    if (study.getCode() == null) {
      study.setCode(namingService.generateStudyCode(study));
    }
    study.setActive(true);

    // External study
    if (study.getCollaborator() != null && !StringUtils.hasText(study.getExternalCode())) {
      study.setExternalCode(namingService.generateExternalStudyCode(study));
    }

    // Get the program
    Program program =
        programRepository
            .findById(study.getProgram().getId())
            .orElseThrow(
                () ->
                    new RecordNotFoundException("Invalid program: " + study.getProgram().getId()));

    // Create the study storage folder
    StorageDriveFolder parentFolder = options.getParentFolder();
    if (parentFolder == null) {
      parentFolder = program.getStorageFolders().stream()
          .filter(f -> f.isPrimary())
          .findFirst()
          .orElseThrow(() -> new RecordNotFoundException("No primary storage folder found for program: "
              + program.getName()))
          .getStorageDriveFolder();
    }
    StudyStorageFolder folder = this.createStudyStorageFolder(study, parentFolder);
    folder.setPrimary(true);
    study.addStudyStorageFolder(folder);

    // Create the ELN folder
    NotebookEntry studySummaryEntry = null;
    if (notebookEntryService != null && options.isUseNotebook()) {

      LOGGER.debug("Creating ELN folder for study: " + study.getName());

      ELNFolder elnFolder = this.createStudyElnFolder(study, program);
      study.setNotebookFolder(elnFolder);

      // Get the template
      NotebookTemplate template = null;
      if (StringUtils.hasText(options.getNotebookTemplateId())) {
        Optional<NotebookTemplate> templateOptional =
            notebookEntryService.findEntryTemplateById(options.getNotebookTemplateId());
        if (templateOptional.isPresent()) {
          template = templateOptional.get();
        } else {
          LOGGER.warn("Could not find notebook template with ID: " + options.getNotebookTemplateId());
        }
      }

      if (!study.isLegacy()) {
        studySummaryEntry = notebookEntryService.createStudyNotebookEntry(study, template);
      }
    } else {
      study.setNotebookFolder(null);
    }

    // Persist the record
    try {
      studyRepository.save(study);
      LOGGER.info(
          String.format(
              "Successfully created new study with code %s and ID %s",
              study.getCode(), study.getId()));
    } catch (ConstraintViolationException e) {
      throw new InvalidConstraintException(e);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    // Git repository
    GitRepository gitRepository = null;
    if (gitService != null && options.isUseGit()) {
      LOGGER.debug("Creating Git repository for study: " + study.getName());
      try {
        gitRepository = gitService.createStudyRepository(study);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to create Git repository for study: " + study.getCode());
      }
    }

    // Additional folders
    for (StorageDriveFolder folderOption : options.getAdditionalFolders()) {
      LOGGER.debug("Creating additional folder for study: " + folder.toString());
      StudyStorageFolder additionalFolder =
          this.createStudyStorageFolder(study, folderOption);
      additionalFolder.setPrimary(false);
      study.addStudyStorageFolder(additionalFolder);
    }


//    if (options.isUseS3() && options.getS3LocationId() != null) {
//      LOGGER.debug("Creating S3 folder for study: " + study.getCode());
//      try {
//        FileStorageLocation s3Location = storageLocationService.findById(options.getS3LocationId())
//            .orElseThrow(() -> new RecordNotFoundException("Invalid S3 location ID: "
//                + options.getS3LocationId()));
//        StudyStorageService s3Service = storageLocationService.lookupStudyStorageService(s3Location);
//        StorageFolder storageFolder = s3Service.createFolder(s3Location, study);
//        FileStoreFolder s3Folder = fileStoreFolderRepository
//            .save(FileStoreFolder.from(s3Location, storageFolder));
//        study.addStudyStorageFolder(s3Folder);
//        studyRepository.save(study);
//      } catch (StudyStorageException e) {
//        e.printStackTrace();
//        LOGGER.error("Failed to create S3 folder for study: " + study.getCode(), e);
//      }
//    }

    // Add a links to extra resources
    if (studySummaryEntry != null) {
      try {
        ExternalLink entryLink = new ExternalLink();
        entryLink.setStudy(study);
        entryLink.setLabel("Summary ELN Entry");
        entryLink.setUrl(new URL(studySummaryEntry.getUrl()));
        study.addExternalLink(entryLink);
        studyRepository.save(study);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to create link to ELN entry.");
      }
    }

    if (gitRepository != null) {
      try {
        ExternalLink entryLink = new ExternalLink();
        entryLink.setStudy(study);
        entryLink.setLabel("Git Repository");
        entryLink.setUrl(new URL(gitRepository.getWebUrl()));
        study.addExternalLink(entryLink);
        studyRepository.save(study);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to create link to Git repository.");
      }
    }

  }

  /**
   * Updates an existing study.
   *
   * @param updated existing study
   */
  @Transactional
  public void update(Study updated) {
    LOGGER.info("Attempting to update existing study with code: " + updated.getCode());
    Study study = studyRepository.getById(updated.getId());

    study.setDescription(updated.getDescription());
    study.setExternalCode(updated.getExternalCode());
    study.setStatus(updated.getStatus());
    study.setStartDate(updated.getStartDate());
    study.setEndDate(updated.getEndDate());
    study.setOwner(updated.getOwner());
    study.setUsers(updated.getUsers());
    study.setKeywords(updated.getKeywords());
    study.setAttributes(updated.getAttributes());

    // Collaborator changes
    if (study.getCollaborator() == null && updated.getCollaborator() != null) {
      study.setCollaborator(updated.getCollaborator());
      if (!StringUtils.hasText(updated.getExternalCode())) {
        study.setExternalCode(namingService.generateExternalStudyCode(study));
      } else {
        study.setExternalCode(updated.getExternalCode());
      }
    } else if (study.getCollaborator() != null && updated.getCollaborator() == null) {
      study.setCollaborator(null);
      study.setExternalCode(null);
    }

    studyRepository.save(study);
  }

  /**
   * Deletes the given study, identifies by its primary key ID.
   *
   * @param study study to be deleted
   */
  @Transactional
  public void delete(Study study) {
    Study s = studyRepository.getById(study.getId());
    s.setActive(false);
    studyRepository.save(s);
  }

  /**
   * Updates the status of the study with the provided PKID to the provided status.
   *
   * @param study study
   * @param status status to set
   */
  @Transactional
  public void updateStatus(Study study, Status status) {
    Study s = studyRepository.getById(study.getId());
    s.setStatus(status);
    if (status.equals(Status.COMPLETE) && study.getEndDate() == null) {
      s.setEndDate(new Date());
    }
    studyRepository.save(s);
  }

  /**
   * Searches the study repository using the provided keyword and returns matching {@link Study}
   * records.
   *
   * @param keyword keyword to search for
   * @return list of matching studies
   */
  @Transactional
  public List<Study> search(String keyword) {
    return studyRepository.findByNameOrCodeLike(keyword);
  }

  /**
   * Checks to see whether the study with the provided ID exists.
   *
   * @param studyId study ID
   * @return true if the study exists
   */
  public boolean exists(Long studyId) {
    return studyRepository.existsById(studyId);
  }

  /**
   * Manually updates a study's {@code updatedAt} and {@code lastModifiedBy} fields.
   *
   * @param study study
   * @param user user
   */
  @Transactional
  public void markAsUpdated(Study study, User user) {
    Study s = studyRepository.getById(study.getId());
    s.setLastModifiedBy(user);
    s.setUpdatedAt(new Date());
    studyRepository.save(s);
  }

  /** Counting number of studies created before/after/between given dates. */
  public long count() {
    return studyRepository.count();
  }

  public long countFromDate(Date startDate) {
    return studyRepository.countByCreatedAtAfter(startDate);
  }

  public long countBeforeDate(Date endDate) {
    return studyRepository.countByCreatedAtBefore(endDate);
  }

  public long countBetweenDates(Date startDate, Date endDate) {
    return studyRepository.countByCreatedAtBetween(startDate, endDate);
  }

  public long countUserActiveStudies(User user) {
    return studyRepository.countActiveUserStudies(user.getId());
  }

  public long countUserCompleteStudies(User user) {
    return studyRepository.countCompleteUserStudies(user.getId());
  }

  public long countByProgram(Program program) {
    return studyRepository.countByProgram(program);
  }

  public long countByProgramAfterDate(Program program, Date date) {
    return studyRepository.countByProgramAndCreatedAtAfter(program, date);
  }

  @Transactional
  public void repairStorageFolder(Study study) {

    // TODO
    // Get the location and storage service
//    FileStorageLocation location;
//    StudyStorageService studyStorageService;
//    try {
//      location = storageLocationService.findDefaultStudyLocation();
//      studyStorageService = storageLocationService.lookupStudyStorageService(location);
//    } catch (FileStorageException e) {
//      e.printStackTrace();
//      throw new StudyTrackerException("Could not find default storage location or service", e);
//    }
//
//    // Find or create the storage folder
//    StorageFolder folder;
//    try {
//      folder = studyStorageService.findFolder(location, study);
//    } catch (StudyStorageNotFoundException e) {
//      LOGGER.warn("Storage folder not found for study: " + study.getCode());
//      e.printStackTrace();
//      throw new StudyTrackerException(e);
//    }
//
//    // Check if a folder record exists in the database
//    List<FileStoreFolder> folders = fileStoreFolderRepository
//        .findByPath(location.getId(), folder.getPath());
//    FileStoreFolder fileStoreFolder = null;
//    if (!folders.isEmpty()) {
//      fileStoreFolder = folders.get(0);
//    }
//
//    // Study has no folder record associated
//    if (study.getPrimaryStorageFolder() == null) {
//      if (fileStoreFolder == null) {
//        LOGGER.info("Repairing study folder with no record.");
//        fileStoreFolder = FileStoreFolder.from(location, folder);
//      }
//      study.setPrimaryStorageFolder(fileStoreFolder);
//      if (study.getStorageFolders().stream().noneMatch(f -> (
//          f.getFileStorageLocation().getId().equals(location.getId())
//              && f.getPath().equals(folder.getPath())))) {
//        study.getStorageFolders().add(fileStoreFolder);
//      }
//      studyRepository.save(study);
//    }
//
//    // Study does have a folder record, but it is malformed
//    else {
//      LOGGER.info("Repairing malformed study folder record.");
//      FileStoreFolder f = fileStoreFolderRepository.getById(study.getPrimaryStorageFolder().getId());
//      f.setFileStorageLocation(location);
//      f.setName(folder.getName());
//      f.setPath(folder.getPath());
//      f.setUrl(folder.getUrl());
//      fileStoreFolderRepository.save(f);
//    }

  }

  @Transactional
  public void repairElnFolder(Study study) {

    // Check to see if the folder exists and create a new one if necessary
    Optional<NotebookFolder> optional = notebookFolderService.findStudyFolder(study);
    NotebookFolder folder = optional.orElseGet(() -> notebookFolderService.createStudyFolder(study));

    // Update the record
    ELNFolder f;
    boolean isNew = false;
    try {
      f = elnFolderRepository.getById(study.getNotebookFolder().getId());
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
      Study s = studyRepository.getById(study.getId());
      s.setNotebookFolder(f);
      studyRepository.save(s);
    }
  }

  @Autowired
  public void setStudyRepository(StudyRepository studyRepository) {
    this.studyRepository = studyRepository;
  }

  @Autowired
  public void setProgramRepository(ProgramRepository programRepository) {
    this.programRepository = programRepository;
  }

  @Autowired
  public void setStorageServiceLookup(
      StudyStorageServiceLookup storageServiceLookup) {
    this.storageServiceLookup = storageServiceLookup;
  }

  @Autowired
  public void setStorageDriveFolderService(
      StorageDriveFolderService storageDriveFolderService) {
    this.storageDriveFolderService = storageDriveFolderService;
  }

  @Autowired(required = false)
  public void setNotebookEntryService(NotebookEntryService notebookEntryService) {
    this.notebookEntryService = notebookEntryService;
  }

  @Autowired
  public void setNamingService(NamingService namingService) {
    this.namingService = namingService;
  }


  @Autowired
  public void setElnFolderRepository(ELNFolderRepository elnFolderRepository) {
    this.elnFolderRepository = elnFolderRepository;
  }

  @Autowired(required = false)
  public void setNotebookFolderService(NotebookFolderService notebookFolderService) {
    this.notebookFolderService = notebookFolderService;
  }

  @Autowired(required = false)
  public void setGitService(GitService gitService) {
    this.gitService = gitService;
  }

}
