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

import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.repository.StudyConclusionsRepository;
import io.studytracker.repository.StudyRepository;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyConclusionsService {

  @Autowired private StudyConclusionsRepository studyConclusionsRepository;

  @Autowired private StudyRepository studyRepository;

  public Page<StudyConclusions> findAll(Pageable pageable) {
    return studyConclusionsRepository.findAll(pageable);
  }

  public Optional<StudyConclusions> findById(Long id) {
    return studyConclusionsRepository.findById(id);
  }

  public Optional<StudyConclusions> findStudyConclusions(Study study) {
    return studyConclusionsRepository.findByStudyId(study.getId());
  }

  @Transactional
  public StudyConclusions addStudyConclusions(Study study, StudyConclusions conclusions) {
    conclusions.setStudy(study);
    studyConclusionsRepository.save(conclusions);
    studyRepository.save(study);
    return conclusions;
  }

  @Transactional
  public StudyConclusions updateStudyConclusions(Study study, StudyConclusions conclusions) {
    StudyConclusions c = studyConclusionsRepository.getById(conclusions.getId());
    c.setContent(conclusions.getContent());
    studyConclusionsRepository.save(c);
    Study s = studyRepository.getById(study.getId());
    s.setUpdatedAt(new Date());
    studyRepository.save(s);
    return c;
  }

  @Transactional
  public void deleteStudyConclusions(Study study) {
    study.setConclusions(null);
    //    Optional<StudyConclusions> optional =
    // studyConclusionsRepository.findByStudyId(study.getId());
    //    if (!optional.isPresent()) {
    //        throw new RecordNotFoundException(String.format("Conclusions not found for study: %s",
    // study.getCode()));
    //    }
    //    studyConclusionsRepository.deleteById(optional.get().getId());
    studyRepository.save(study);
  }
}
