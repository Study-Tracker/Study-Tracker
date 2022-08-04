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
import io.studytracker.eln.StudyNotebookService;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.FileStoreFolder;
import io.studytracker.model.Program;
import io.studytracker.repository.ELNFolderRepository;
import io.studytracker.repository.FileStoreFolderRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.storage.StorageFolder;
import io.studytracker.storage.StudyStorageService;
import io.studytracker.storage.exception.StudyStorageException;
import io.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

  private ProgramRepository programRepository;

  private StudyStorageService studyStorageService;

  private StudyNotebookService studyNotebookService;

  private FileStoreFolderRepository fileStoreFolderRepository;

  private ELNFolderRepository elnFolderRepository;

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
  public Program create(Program program) {
    LOGGER.info("Creating new program with name: " + program.getName());

    program.setCreatedAt(new Date());
    program.setUpdatedAt(new Date());

    // Create the storage folder
    try {
      StorageFolder storageFolder = studyStorageService.createProgramFolder(program);
      program.setStorageFolder(FileStoreFolder.from(storageFolder));
    } catch (StudyStorageException e) {
      throw new StudyTrackerException(e);
    }

    // Create the notebook folder
    if (studyNotebookService != null) {
      try {
        NotebookFolder notebookFolder = studyNotebookService.createProgramFolder(program);
        program.setNotebookFolder(ELNFolder.from(notebookFolder));
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    } else {
      program.setNotebookFolder(null);
    }

    return programRepository.save(program);

  }

  @Transactional
  public Program update(Program program) {
    LOGGER.info("Updating program with name: " + program.getName());

    Program p = programRepository.getById(program.getId());
    p.setDescription(program.getDescription());
    p.setActive(program.isActive());
    p.setAttributes(program.getAttributes());
    programRepository.save(p);

    if (program.getNotebookFolder() != null) {
      ELNFolder f = elnFolderRepository.getById(program.getNotebookFolder().getId());
      ELNFolder folder = program.getNotebookFolder();
      f.setReferenceId(folder.getReferenceId());
      f.setUrl(folder.getUrl());
      f.setName(folder.getName());
      elnFolderRepository.save(f);
    }

    return programRepository.findById(program.getId())
        .orElseThrow(() -> new StudyTrackerException("Program not found: " + program.getId()));
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

    // Find or create the storage folder
    StorageFolder folder;
    try {
      folder = studyStorageService.getProgramFolder(program, false);
    } catch (StudyStorageNotFoundException e) {
      try {
        folder = studyStorageService.createProgramFolder(program);
      } catch (Exception ex) {
        throw new StudyTrackerException(ex);
      }
    }

    // Update the  program record
    FileStoreFolder f = fileStoreFolderRepository.getById(program.getStorageFolder().getId());
    f.setName(folder.getName());
    f.setPath(folder.getPath());
    f.setUrl(folder.getUrl());
    fileStoreFolderRepository.save(f);
  }

  @Transactional
  public void repairElnFolder(Program program) {

    // Check to see if the folder exists and create a new one if necessary
    NotebookFolder folder;
    Optional<NotebookFolder> optional = studyNotebookService.findProgramFolder(program);
    if (optional.isPresent()) {
      folder = optional.get();
    } else {
      folder = studyNotebookService.createProgramFolder(program);
    }

    // Update the record
    ELNFolder f = elnFolderRepository.getById(program.getNotebookFolder().getId());
    f.setName(folder.getName());
    f.setPath(folder.getPath());
    f.setUrl(folder.getUrl());
    f.setReferenceId(folder.getReferenceId());
    elnFolderRepository.save(f);
  }

  @Autowired
  public void setProgramRepository(ProgramRepository programRepository) {
    this.programRepository = programRepository;
  }

  @Autowired
  public void setStudyStorageService(StudyStorageService studyStorageService) {
    this.studyStorageService = studyStorageService;
  }

  @Autowired(required = false)
  public void setStudyNotebookService(StudyNotebookService studyNotebookService) {
    this.studyNotebookService = studyNotebookService;
  }

  @Autowired
  public void setFileStoreFolderRepository(FileStoreFolderRepository fileStoreFolderRepository) {
    this.fileStoreFolderRepository = fileStoreFolderRepository;
  }

  @Autowired
  public void setElnFolderRepository(ELNFolderRepository elnFolderRepository) {
    this.elnFolderRepository = elnFolderRepository;
  }
}
