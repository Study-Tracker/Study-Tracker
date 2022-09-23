/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.model.Keyword;
import io.studytracker.model.Study;
import io.studytracker.repository.KeywordRepository;
import io.studytracker.repository.StudyRepository;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyKeywordsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyKeywordsService.class);

  @Autowired private StudyRepository studyRepository;
  @Autowired private KeywordRepository keywordRepository;

  public List<Keyword> findStudyKeywords(Study study) {
    return keywordRepository.findByStudyId(study.getId());
  }

  @Transactional
  public void updateStudyKeywords(Study study, Set<Keyword> keywords) {
    LOGGER.info("Updating study keywords for study {}", study.getCode());
    Study s = studyRepository.getById(study.getId());
    s.setKeywords(keywords);
    studyRepository.save(s);
  }


}
