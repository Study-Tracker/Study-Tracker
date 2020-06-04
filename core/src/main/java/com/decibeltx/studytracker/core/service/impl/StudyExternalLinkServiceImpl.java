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
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.ExternalLink;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.StudyExternalLinkService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyExternalLinkServiceImpl implements StudyExternalLinkService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyExternalLinkServiceImpl.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyEventPublisher studyEventPublisher;

  @Override
  public List<ExternalLink> findAllStudyExternalLinks(Study study) {
    return study.getExternalLinks();
  }

  @Override
  public Optional<ExternalLink> findStudyExternalLinkById(Study study, String id) {
    List<ExternalLink> links = study.getExternalLinks().stream()
        .filter(s -> s.getId().equals(id))
        .collect(Collectors.toList());
    if (links.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(links.get(0));
    }
  }

  @Override
  public ExternalLink addStudyExternalLink(Study study, ExternalLink externalLink) {
    externalLink.setId(UUID.randomUUID().toString());
    study.getExternalLinks().add(externalLink);
    LOGGER.info(String.format("Adding new external link for study %s: %s",
        study.getCode(), externalLink));
    studyRepository.save(study);
    studyEventPublisher.publishNewExternalLinkEvent(study, study.getLastModifiedBy(), externalLink);
    return externalLink;
  }

  @Override
  public ExternalLink updateStudyExternalLink(Study study, ExternalLink externalLink) {
    boolean failed = true;
    for (ExternalLink link : study.getExternalLinks()) {
      if (link.getId().equals(externalLink.getId())) {
        link.setLabel(externalLink.getLabel());
        link.setUrl(externalLink.getUrl());
        failed = false;
      }
    }
    if (failed) {
      throw new RecordNotFoundException(
          String.format("Cannot find external link with ID %s for study %s",
              externalLink.getId(), study.getCode()));
    }
    studyRepository.save(study);
    studyEventPublisher
        .publishUpdatedExternalLinkEvent(study, study.getLastModifiedBy(), externalLink);
    return externalLink;
  }

  @Override
  public void deleteStudyExternalLink(Study study, String id) {
    Optional<ExternalLink> optional = this.findStudyExternalLinkById(study, id);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException(
          String.format("Cannot find external link with ID %s for study %s", id, study.getCode())
      );
    }
    List<ExternalLink> links = study.getExternalLinks().stream()
        .filter(s -> !s.getId().equals(id))
        .collect(Collectors.toList());
    study.setExternalLinks(links);
    LOGGER
        .info(String.format("Removing external link with ID %s for study %s", id, study.getCode()));
    studyRepository.save(study);
    studyEventPublisher.publishDeletedExternalLinkEvent(study, study.getLastModifiedBy());
  }

}
