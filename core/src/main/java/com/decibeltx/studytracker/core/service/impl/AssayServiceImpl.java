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

import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.AssayRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.AssayService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssayServiceImpl implements AssayService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayServiceImpl.class);

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Override
  public Optional<Assay> findById(String id) {
    return assayRepository.findById(id);
  }

  @Override
  public Optional<Assay> findByCode(String code) {
    return assayRepository.findByCode(code);
  }

  @Override
  public List<Assay> findByStudyId(String studyId) {
    return assayRepository.findByStudyId(studyId);
  }

  @Override
  public List<Assay> findAll() {
    return assayRepository.findAll();
  }

  @Override
  public void create(Assay assay) {
    LOGGER.info("Creating new assay record with name: " + assay.getName());
    Study study = studyRepository.findById(assay.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);
    assay.setCode(generateAssayCode(assay));
    assay.setActive(true);
    assayRepository.insert(assay);
    study.getAssays().add(assay);
    studyRepository.save(study);
  }

  @Override
  public void update(Assay updated) {
    LOGGER.info("Updating assay record with code: " + updated.getCode());
    assayRepository.findById(updated.getId())
        .orElseThrow(RecordNotFoundException::new);
    assayRepository.save(updated);
  }

  @Override
  public void delete(Assay assay) {
    assay.setActive(false);
    assayRepository.save(assay);
  }

  @Override
  public void updateStatus(Assay assay, Status status) {
    assay.setStatus(status);
    assayRepository.save(assay);
  }

  @Override
  public String generateAssayCode(Assay assay) {
    Study study = assay.getStudy();
    String prefix = study.getProgram().getCode() + "-";
    int count = assayRepository.findByCodePrefix(prefix).size();
    return study.getCode() + "-" + String.format("%05d", count + 1);
  }
}
