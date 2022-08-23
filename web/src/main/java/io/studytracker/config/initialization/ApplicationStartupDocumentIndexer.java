package io.studytracker.config.initialization;

import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupDocumentIndexer implements ApplicationRunner {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ApplicationStartupDocumentIndexer.class);

  @Autowired private StudyRepository studyRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired(required = false)
  private SearchService searchService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (searchService != null) {
      LOGGER.info("Running startup search document indexing...");

      // Index studies
      int studyCount = 0;
      for (Study study : studyRepository.findAllWithDetails()) {
        searchService.indexStudy(study);
        studyCount = studyCount + 1;
      }

      // Index assays
      int assayCount = 0;
      for (Assay assay : assayRepository.findAllWithDetails()) {
        searchService.indexAssay(assay);
        assayCount = assayCount + 1;
      }

      LOGGER.info("Document indexing complete. Indexed {} studies and {} assays",
          studyCount, assayCount);
    } else {
      LOGGER.warn("StudySearchService is not defined. No study search indexing will occur.");
    }
  }
}
