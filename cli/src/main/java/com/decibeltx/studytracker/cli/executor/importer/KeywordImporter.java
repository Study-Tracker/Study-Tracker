package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.core.model.Keyword;
import com.decibeltx.studytracker.core.service.KeywordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeywordImporter extends RecordImporter<Keyword> {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeywordImporter.class);

  @Autowired
  private KeywordService keywordService;

  public KeywordImporter() {
    super(Keyword.class);
  }

  @Override
  void importRecord(Keyword record) throws Exception {
    if (keywordService.findByKeywordAndCategory(record.getKeyword(), record.getCategory())
        .isPresent()) {
      LOGGER.warn(String.format("Keyword '%s' in category '%s' already exists. Skipping record.",
          record.getKeyword(), record.getCategory()));
    } else {
      this.validate(record);
      keywordService.create(record);
    }
  }
}
