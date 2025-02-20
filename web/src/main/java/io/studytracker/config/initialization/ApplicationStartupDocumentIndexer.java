/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.config.initialization;

import io.studytracker.config.ConfigOrder;
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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(ConfigOrder.AFTER_INIT)
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
