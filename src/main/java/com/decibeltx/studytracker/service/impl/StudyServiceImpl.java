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
import com.decibeltx.studytracker.exception.DuplicateRecordException;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.service.NamingService;
import com.decibeltx.studytracker.service.StudyService;
import com.decibeltx.studytracker.storage.StorageFolder;
import com.decibeltx.studytracker.storage.StudyStorageService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class StudyServiceImpl implements StudyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceImpl.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired(required = false)
  private StudyNotebookService notebookService;

  @Autowired
  private NamingService namingService;

  @Override
  public Optional<Study> findById(String id) {
    return studyRepository.findById(id);
  }

  @Override
  public List<Study> findAll() {
    return studyRepository.findAll();
  }

  @Override
  public List<Study> findByProgram(Program program) {
    return studyRepository.findByProgramId(program.getId());
  }

  @Override
  public List<Study> findByName(String name) {
    return studyRepository.findByName(name);
  }

  @Override
  public Optional<Study> findByCode(String code) {
    return studyRepository.findByCode(code);
  }

  @Override
  public Optional<Study> findByExternalCode(String code) {
    return studyRepository.findByExternalCode(code);
  }

  @Override
  public void create(Study study) {

    LOGGER.info("Attempting to create new study with name: " + study.getName());

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

    if (study.getCode() == null) {
      study.setCode(namingService.generateStudyCode(study));
    }
    study.setActive(true);

    // External study
    if (study.getCollaborator() != null && StringUtils.isEmpty(study.getExternalCode())) {
      study.setExternalCode(namingService.generateExternalStudyCode(study));
    }

    try {
      studyRepository.insert(study);
    } catch (Exception e) {
      if (e instanceof ConstraintViolationException) {
        throw new InvalidConstraintException(e);
      } else {
        throw e;
      }
    }

    // Create the study storage folder
    try {
      studyStorageService.createStudyFolder(study);
      StorageFolder folder = studyStorageService.getStudyFolder(study);
      study.setStorageFolder(folder);
      study.setUpdatedAt(new Date());
      studyRepository.save(study);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Failed to create storage folder for study: " + study.getCode());
    }

    // Create the ELN folder
    LOGGER.warn(String.format("Creating ELN entry for study: %s", study.getCode()));
    if (study.isLegacy()) {
      LOGGER.warn(String.format("Legacy Study : %s", study.getCode()));
      NotebookFolder notebookFolder = study.getNotebookFolder();
      notebookFolder.setName(namingService.getStudyNotebookFolderName(study));
      study.setNotebookFolder(notebookFolder);
      study.setUpdatedAt(new Date());
      studyRepository.save(study);
    } else {
      if (notebookService != null) {
        try {
          NotebookFolder notebookFolder = notebookService.createStudyFolder(study);
          study.setNotebookFolder(notebookFolder);
          study.setUpdatedAt(new Date());
          studyRepository.save(study);
        } catch (Exception e) {
          e.printStackTrace();
          LOGGER.error("Failed to create notebook entry for study: " + study.getCode());
        }
      }
    }

    LOGGER.info(String.format("Successfully created new study with code %s and ID %s",
        study.getCode(), study.getId()));

  }

  @Override
  public void update(Study updated) {
    LOGGER.info("Attempting to update existing study with code: " + updated.getCode());
    Study study = studyRepository.findById(updated.getId())
        .orElseThrow(RecordNotFoundException::new);

    study.setDescription(updated.getDescription());
    study.setStatus(updated.getStatus());
    study.setStartDate(updated.getStartDate());
    study.setEndDate(updated.getEndDate());
    study.setOwner(updated.getOwner());
    study.setUsers(updated.getUsers());
    study.setKeywords(updated.getKeywords());

    // Collaborator changes
    if (study.getCollaborator() == null && updated.getCollaborator() != null) {
      study.setCollaborator(updated.getCollaborator());
      if (StringUtils.isEmpty(updated.getExternalCode())) {
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

  @Override
  public void delete(Study study) {
    study.setActive(false);
    studyRepository.save(study);
  }

  @Override
  public void updateStatus(Study study, Status status) {
    study.setStatus(status);
    if (status.equals(Status.COMPLETE) && study.getEndDate() == null) {
      study.setEndDate(new Date());
    }
    studyRepository.save(study);
  }

  @Override
  public List<Study> search(String keyword) {
    return studyRepository.findByNameOrCodeLike(keyword);
  }

  @Override
  public long count() {
    return studyRepository.count();
  }

  @Override
  public long countFromDate(Date startDate) {
    return studyRepository.countByCreatedAtAfter(startDate);
  }

  @Override
  public long countBeforeDate(Date endDate) {
    return studyRepository.countByCreatedAtBefore(endDate);
  }

  @Override
  public long countBetweenDates(Date startDate, Date endDate) {
    return studyRepository.countByCreatedAtBetween(startDate, endDate);
  }

}
