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

import com.decibeltx.studytracker.model.ExternalLink;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.ExternalLinkRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyExternalLinkService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyExternalLinkService.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private ExternalLinkRepository externalLinkRepository;

  public List<ExternalLink> findAllStudyExternalLinks(Study study) {
    return externalLinkRepository.findByStudyId(study.getId());
  }

  public Optional<ExternalLink> findById(Long id) {
    return externalLinkRepository.findById(id);
  }

  @Transactional
  public void addStudyExternalLink(Study study, ExternalLink externalLink) {
    LOGGER.info(String.format("Adding new external link for study %s: %s",
        study.getCode(), externalLink));
    study.addExternalLink(externalLink);
    studyRepository.save(study);
  }

  @Transactional
  public void updateStudyExternalLink(Study study, ExternalLink externalLink) {
    ExternalLink l = externalLinkRepository.getById(externalLink.getId());
    l.setUrl(externalLink.getUrl());
    l.setLabel(externalLink.getLabel());
    externalLinkRepository.save(l);
  }

  @Transactional
  public void deleteStudyExternalLink(Study study, Long linkId) {
    study.removeExternalLink(linkId);
//    externalLinkRepository.deleteById(linkId);
//    externalLinkRepository.flush();
//    Study s = studyRepository.getById(study.getId());
//    s.setUpdatedAt(new Date());
    studyRepository.save(study);
  }

}
