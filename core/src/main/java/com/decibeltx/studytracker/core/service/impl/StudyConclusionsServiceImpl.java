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

import com.decibeltx.studytracker.core.model.Conclusions;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.StudyConclusionsService;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyConclusionsServiceImpl implements StudyConclusionsService {

  @Autowired
  private StudyRepository studyRepository;

  @Override
  public Optional<Conclusions> findStudyConclusions(Study study) {
    return Optional.ofNullable(study.getConclusions());
  }

  @Override
  public Conclusions addStudyConclusions(Study study, Conclusions conclusions) {
    conclusions.setCreatedAt(new Date());
    conclusions.setId(UUID.randomUUID().toString());
    study.setConclusions(conclusions);
    studyRepository.save(study);
    return conclusions;
  }

  @Override
  public Conclusions updateStudyConclusions(Study study, Conclusions conclusions) {
    conclusions.setUpdatedAt(new Date());
    study.setConclusions(conclusions);
    studyRepository.save(study);
    return conclusions;
  }

  @Override
  public void deleteStudyConclusions(Study study) {
    study.setConclusions(null);
    studyRepository.save(study);
  }

}
