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

import com.decibeltx.studytracker.core.events.StudyEventPublisher;
import com.decibeltx.studytracker.core.exception.DuplicateRecordException;
import com.decibeltx.studytracker.core.exception.InvalidConstraintException;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.ProgramService;
import com.decibeltx.studytracker.core.service.StudyService;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudyServiceImpl implements StudyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceImpl.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ProgramService programService;

  @Autowired
  private StudyEventPublisher studyEventPublisher;

  @Autowired
  private Environment environment;

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
      study.setCode(this.generateStudyCode(study));
    }
    study.setActive(true);

    // External study
    if (study.getCollaborator() != null && StringUtils.isEmpty(study.getExternalCode())) {
      study.setExternalCode(this.generateExternalStudyCode(study));
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

    LOGGER.info(String.format("Successfully created new study with code %s and ID %s",
        study.getCode(), study.getId()));

    // Publish events
    studyEventPublisher.publishNewStudyEvent(study, study.getCreatedBy());

  }

  @Override
  public void update(Study study) {
    LOGGER.info("Attempting to update existing study with code: " + study.getCode());
    studyRepository.findById(study.getId()).orElseThrow(RecordNotFoundException::new);
    studyRepository.save(study);
    studyEventPublisher.publishUpdatedStudyEvent(study, study.getLastModifiedBy());
  }

  @Override
  public void delete(Study study) {
    study.setActive(false);
    studyRepository.save(study);
    studyEventPublisher.publishDeletedStudyEvent(study, study.getLastModifiedBy());
  }

  @Override
  public String generateStudyCode(Study study) {
    if (study.isLegacy()) {
      throw new StudyTrackerException("Legacy studies do not recieve new study codes.");
    }
    Program program = study.getProgram();
    Integer count = environment.containsProperty("study.code-counter-start")
        ? environment.getRequiredProperty("study.code-counter-start", Integer.class)
        : 10001;
    for (Program p : programService.findByCode(program.getCode())) {
      count = count + (studyRepository.findActiveProgramStudies(p.getId())).size();
    }
    return program.getCode() + "-" + count.toString();
  }

  @Override
  public String generateExternalStudyCode(Study study) {
    Collaborator collaborator = study.getCollaborator();
    if (collaborator == null) {
      throw new StudyTrackerException("External studies require a valid collaborator reference.");
    }
    Integer count =
        1 + studyRepository.findByExternalCodePrefix(collaborator.getCode() + "-").size();
    return collaborator.getCode() + "-" + String.format("%05d", count);
  }

  @Override
  public void updateStatus(Study study, Status status) {
    Status oldStatus = study.getStatus();
    study.setStatus(status);
    studyRepository.save(study);
    studyEventPublisher
        .publishStudyStatusChangedEvent(study, study.getLastModifiedBy(), oldStatus, status);
  }

  @Override
  public List<Study> search(String keyword) {
    return studyRepository.findByNameOrCodeLike(keyword);
  }

}
