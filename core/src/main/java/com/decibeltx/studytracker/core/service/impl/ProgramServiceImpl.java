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

package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.eln.NotebookFolder;
import com.decibeltx.studytracker.core.eln.StudyNotebookService;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.repository.ProgramRepository;
import com.decibeltx.studytracker.core.service.ProgramService;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.core.storage.StudyStorageService;
import com.decibeltx.studytracker.core.storage.exception.StudyStorageException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramServiceImpl implements ProgramService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgramServiceImpl.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired(required = false)
  private StudyNotebookService studyNotebookService;

  @Override
  public Optional<Program> findById(String id) {
    return programRepository.findById(id);
  }

  @Override
  public Optional<Program> findByName(String name) {
    return programRepository.findByName(name);
  }

  @Override
  public List<Program> findAll() {
    return programRepository.findAll();
  }

  @Override
  public List<Program> findByCode(String code) {
    return programRepository.findByCode(code);
  }

  @Override
  public void create(Program program) {
    LOGGER.info("Creating new program with name: " + program.getName());

    program.setCreatedAt(new Date());
    program.setUpdatedAt(new Date());

    // Create the storage folder
    try {
      StorageFolder storageFolder = studyStorageService.createProgramFolder(program);
      program.setStorageFolder(storageFolder);
    } catch (StudyStorageException e) {
      throw new StudyTrackerException(e);
    }

    // Create the notebook folder
    if (studyNotebookService != null) {
      try {
        NotebookFolder notebookFolder = studyNotebookService.createProgramFolder(program);
        program.setNotebookFolder(notebookFolder);
      } catch (Exception e) {
        throw new StudyTrackerException(e);
      }
    }

    programRepository.insert(program);
  }

  @Override
  public void update(Program program) {
    LOGGER.info("Updating program with name: " + program.getName());
    programRepository.findById(program.getId()).orElseThrow(RecordNotFoundException::new);
    programRepository.save(program);
  }

  @Override
  public void delete(Program program) {
    LOGGER.info("Innactivating program with name: " + program.getName());
    programRepository.findById(program.getId()).orElseThrow(RecordNotFoundException::new);
    program.setActive(false);
    programRepository.save(program);
  }

  @Override
  public long count() {
    return programRepository.count();
  }

  @Override
  public long countFromDate(Date startDate) {
    return programRepository.countByCreatedAtAfter(startDate);
  }

  @Override
  public long countBeforeDate(Date endDate) {
    return programRepository.countByCreatedAtBefore(endDate);
  }

  @Override
  public long countBetweenDates(Date startDate, Date endDate) {
    return programRepository.countByCreatedAtBetween(startDate, endDate);
  }
}
