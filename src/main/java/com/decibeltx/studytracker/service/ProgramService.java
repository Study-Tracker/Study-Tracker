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
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.decibeltx.studytracker.model.ELNFolder;
import com.decibeltx.studytracker.model.FileStoreFolder;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.repository.FileStoreFolderRepository;
import com.decibeltx.studytracker.repository.ProgramRepository;
import com.decibeltx.studytracker.storage.StorageFolder;
import com.decibeltx.studytracker.storage.StudyStorageService;
import com.decibeltx.studytracker.storage.exception.StudyStorageException;
import com.decibeltx.studytracker.storage.exception.StudyStorageNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgramService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramService.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired(required = false)
  private StudyNotebookService studyNotebookService;

  @Autowired
  private FileStoreFolderRepository fileStoreFolderRepository;

  public Optional<Program> findById(Long id) {
    return programRepository.findById(id);
  }

  public Optional<Program> findByName(String name) {
    return programRepository.findByName(name);
  }

  public List<Program> findAll() {
    return programRepository.findAll();
  }

  public List<Program> findByCode(String code) {
    return programRepository.findByCode(code);
  }

  @Transactional
  public void create(Program program) {
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
    }

    programRepository.save(program);
  }

  @Transactional
  public void update(Program program) {
    LOGGER.info("Updating program with name: " + program.getName());
    Program p = programRepository.getOne(program.getId());
    p.setDescription(program.getDescription());
    p.setActive(program.isActive());
    p.setAttributes(program.getAttributes());
    programRepository.save(p);
  }

  @Transactional
  public void delete(Program program) {
    LOGGER.info("Innactivating program with name: " + program.getName());
    this.delete(program.getId());
  }

  @Transactional
  public void delete(Long programId) {
    Program program = programRepository.getOne(programId);
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
      folder = studyStorageService.getProgramFolder(program);
    } catch (StudyStorageNotFoundException e) {
      try {
        folder = studyStorageService.createProgramFolder(program);
      } catch (Exception ex) {
        throw new StudyTrackerException(ex);
      }
    }

    // Update the  program record
    FileStoreFolder f = fileStoreFolderRepository.getOne(program.getStorageFolder().getId());
    f.setName(folder.getName());
    f.setPath(folder.getPath());
    f.setUrl(folder.getUrl());
    fileStoreFolderRepository.save(f);
  }

}
