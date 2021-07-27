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

import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyConclusions;
import com.decibeltx.studytracker.repository.StudyConclusionsRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyConclusionsService {

  @Autowired
  private StudyConclusionsRepository studyConclusionsRepository;

  @Autowired
  private StudyRepository studyRepository;

  public Optional<StudyConclusions> findStudyConclusions(Study study) {
    return studyConclusionsRepository.findByStudyId(study.getId());
  }

  @Transactional
  public void addStudyConclusions(Study study, StudyConclusions conclusions) {
    conclusions.setStudy(study);
    studyConclusionsRepository.save(conclusions);
    studyRepository.save(study);
  }

  @Transactional
  public void updateStudyConclusions(Study study, StudyConclusions conclusions) {
    StudyConclusions c = studyConclusionsRepository.getOne(conclusions.getId());
    c.setContent(conclusions.getContent());
    studyConclusionsRepository.save(c);
    Study s = studyRepository.getOne(study.getId());
    s.setUpdatedAt(new Date());
    studyRepository.save(s);
  }

  @Transactional
  public void deleteStudyConclusions(Study study) {
    study.setConclusions(null);
//    Optional<StudyConclusions> optional = studyConclusionsRepository.findByStudyId(study.getId());
//    if (!optional.isPresent()) {
//        throw new RecordNotFoundException(String.format("Conclusions not found for study: %s", study.getCode()));
//    }
//    studyConclusionsRepository.deleteById(optional.get().getId());
    studyRepository.save(study);
  }

}
