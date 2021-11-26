package com.decibeltx.studytracker.config.initialization;

import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupStudyIndexer implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationStartupStudyIndexer.class);

  @Autowired
  private StudyRepository studyRepository;

  @Autowired(required = false)
  private SearchService<?, ?> searchService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (searchService != null) {
      LOGGER.info("Running startup full study search indexing...");
      int count = 0;
      for (Study study : studyRepository.findAllWithDetails()) {
        searchService.indexStudy(study);
        count = count + 1;
      }
      LOGGER.info("Study indexing complete. Indexed {} studies", count);
    } else {
      LOGGER.warn("StudySearchService is not defined. No study search indexing will occur.");
    }
  }
}
