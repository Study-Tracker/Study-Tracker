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
