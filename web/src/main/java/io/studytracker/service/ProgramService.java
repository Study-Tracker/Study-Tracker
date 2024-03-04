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

import io.studytracker.benchling.BenchlingNotebookFolderService;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.git.GitService;
import io.studytracker.git.GitServiceLookup;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.GitGroup;
import io.studytracker.model.Program;
import io.studytracker.model.ProgramNotebookFolder;
import io.studytracker.model.ProgramOptions;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.storage.StorageDriveFolderService;
import io.studytracker.storage.StudyStorageService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramService.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private BenchlingNotebookFolderService notebookFolderService;

  @Autowired
  private ELNFolderRepository elnFolderRepository;

  @Autowired
  private GitServiceLookup gitServiceLookup;

  @Autowired
  private StorageDriveFolderService storageDriveFolderService;

  public Optional<Program> findById(Long id) {
    return programRepository.findById(id);
  }

  public Optional<Program> findByName(String name) {
    return programRepository.findByName(name);
  }

  public List<Program> findAll() {
    return programRepository.findAll();
  }

  public Page<Program> findAll(Pageable pageable) {
    return programRepository.findAll(pageable);
  }

  public List<Program> findByCode(String code) {
    return programRepository.findByCode(code);
  }

  @Transactional
  public Program create(@NotNull Program program) {
    LOGGER.info("Creating new program with name: " + program.getName());
    ProgramOptions options = program.getOptions();

    // Create the storage folder
    if (options.isUseStorage() && options.getParentFolder() != null
        && options.getParentFolder().getId() != null) {
      LOGGER.info("Creating storage folder for program: " + program.getName());
      try {
        StorageDriveFolder parentFolder = storageDriveFolderService
            .findById(options.getParentFolder().getId())
            .orElseThrow(() -> new RecordNotFoundException(
                "Parent folder not found: " + options.getParentFolder().getId()));
        StudyStorageService studyStorageService = storageDriveFolderService.lookupStudyStorageService(parentFolder);
        StorageDriveFolder programFolder = studyStorageService.createProgramFolder(parentFolder, program);
        program.addStorageFolder(programFolder, true);
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    } else {
      LOGGER.info("Not creating storage folder for program: " + program.getName());
    }

    // Create the notebook folder
    if (options.isUseNotebook()) {
      LOGGER.info("Creating notebook folder for program: " + program.getName());
      try {
        ELNFolder notebookFolder = notebookFolderService.createProgramFolder(program);
        LOGGER.debug("Created notebook folder: " + notebookFolder);
        program.addNotebookFolder(notebookFolder, true);
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    } else {
      LOGGER.info("Not creating notebook folder for program: " + program.getName());
    }

    programRepository.save(program);
    Program created = programRepository.findById(program.getId())
        .orElseThrow(InvalidRequestException::new);

    // Create the program Git group
    if (options.isUseGit() && options.getGitGroup() != null) {
      try {
        GitGroup gitGroup = options.getGitGroup();
        GitService gitService = gitServiceLookup.lookup(gitGroup.getGitServiceType())
            .orElseThrow(() -> new InvalidRequestException(
                "Git service not found: " + gitGroup.getGitServiceType()));
        GitGroup programGroup =  gitService.createProgramGroup(gitGroup, created);
        program.addGitGroup(programGroup);
        programRepository.save(program);
      } catch (Exception e) {
        LOGGER.warn("Error creating Git group for program: " + program.getName());
        e.printStackTrace();
        throw new StudyTrackerException(e);
      }
    }

    return created;

  }

  @Transactional
  public Program update(@NotNull Program program, @NotNull ProgramOptions options) {
    LOGGER.info("Updating program with name: " + program.getName());

    Program p = programRepository.getById(program.getId());
    p.setDescription(program.getDescription());
    p.setActive(program.isActive());
    p.setAttributes(program.getAttributes());
    programRepository.save(p);

    // Update the notebook folder details
//    if (program.getNotebookFolder() != null) {
//      ELNFolder f = elnFolderRepository.getById(program.getNotebookFolder().getId());
//      ELNFolder folder = program.getNotebookFolder();
//      f.setReferenceId(folder.getReferenceId());
//      f.setUrl(folder.getUrl());
//      f.setName(folder.getName());
//      elnFolderRepository.save(f);
//    }

    // Update the Git group details
    if (options.isUseGit() && options.getGitGroup() != null) {
      try {

        GitGroup parentGroup = options.getGitGroup();
        GitService gitService = gitServiceLookup.lookup(parentGroup.getGitServiceType())
            .orElseThrow(() -> new InvalidRequestException(
                "Git service not found: " + parentGroup.getGitServiceType()));

        // has a new group been added when one did not previously exist?
        if (p.getGitGroups().isEmpty()) {
          LOGGER.info("Adding new Git group for program: " + program.getName());
          GitGroup programGroup =  gitService.createProgramGroup(parentGroup, program);
          p.addGitGroup(programGroup);
          programRepository.save(p);
        }

        // Is the requested group different from the existing one?
        else if (p.getGitGroups().stream()
            .noneMatch(g -> g.getParentGroup().getId().equals(parentGroup.getId()))) {
          LOGGER.info("Updating Git group for program: " + program.getName());
          GitGroup existingGroup = p.getGitGroups().stream().findFirst().get();
          p.removeGitGroup(existingGroup);
          GitGroup programGroup =  gitService.createProgramGroup(parentGroup, program);
          p.addGitGroup(programGroup);
          programRepository.save(p);
        }

        // The requested group is the same as the existing one
        else {
          LOGGER.debug("Git group for program is unchanged: " + program.getName());
        }

      } catch (Exception e) {
        e.printStackTrace();
        throw new StudyTrackerException(e);
      }
    } else if (options.isUseGit() && options.getGitGroup() == null) {
      LOGGER.warn("Git group not specified for program: " + program.getName());
    }

    return programRepository.findById(program.getId())
        .orElseThrow(() -> new StudyTrackerException("Program not found: " + program.getId()));
  }

  public Program update(Program program) {
    return update(program, new ProgramOptions());
  }

  @Transactional
  public void delete(Program program) {
    LOGGER.info("Innactivating program with name: " + program.getName());
    this.delete(program.getId());
  }

  @Transactional
  public void delete(Long programId) {
    Program program = programRepository.getById(programId);
    program.setActive(false);
    programRepository.save(program);
  }

  public boolean exists(Long id) {
    return programRepository.existsById(id);
  }

  public long count() {
    return programRepository.count();
  }

  public long countFromDate(Date startDate) {
    return programRepository.countByCreatedAtAfter(startDate);
  }

  public long countBeforeDate(Date endDate) {
    return programRepository.countByCreatedAtBefore(endDate);
  }

  public long countBetweenDates(Date startDate, Date endDate) {
    return programRepository.countByCreatedAtBetween(startDate, endDate);
  }

  @Transactional
  public void repairStorageFolder(Program program) {

    // TODO
    throw new InvalidRequestException("Not implemented");

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
//      folder = studyStorageService.findFolder(location, program);
//    } catch (Exception e) {
//      e.printStackTrace();
//      throw new StudyTrackerException(e);
//    }
//
//    // Update the  program record
//    FileStoreFolder f = fileStoreFolderRepository.getById(program.getPrimaryStorageFolder().getId());
//    f.setName(folder.getName());
//    f.setPath(folder.getPath());
//    f.setUrl(folder.getUrl());
//    fileStoreFolderRepository.save(f);
  }

  @Transactional
  public void repairElnFolder(Program program) {

    // Check to see if the folder exists and create a new one if necessary
    ELNFolder folder;
    Optional<ELNFolder> optional = notebookFolderService.findPrimaryProgramFolder(program);
    if (optional.isPresent()) {
      folder = optional.get();
    } else {
      folder = notebookFolderService.createProgramFolder(program);
    }

    // Update the record
    ProgramNotebookFolder programFolder = program.getNotebookFolders().stream()
        .filter(f -> f.isPrimary())
        .findFirst()
        .orElse(null);
    if (programFolder != null) {
      ELNFolder f = programFolder.getElnFolder();
      f.setName(folder.getName());
      f.setPath(folder.getPath());
      f.setUrl(folder.getUrl());
      f.setReferenceId(folder.getReferenceId());
      elnFolderRepository.save(f);
    } else {
      elnFolderRepository.save(folder);
      program.addNotebookFolder(folder, true);
      programRepository.save(program);
    }

  }

}
