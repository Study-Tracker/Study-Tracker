package com.decibeltx.studytracker.search.elasticsearch;

import com.decibeltx.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import com.decibeltx.studytracker.mapstruct.mapper.ElasticsearchDocumentMapper;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.search.SearchService;
import com.decibeltx.studytracker.search.StudySearchHits;
import java.util.Arrays;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;

public class ElasticsearchSearchService implements SearchService<ElasticsearchStudyDocument, Long> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSearchService.class);

  @Autowired
  private StudyIndexRepository studyIndexRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ElasticsearchDocumentMapper documentMapper;

  @Override
  public StudySearchHits<ElasticsearchStudyDocument> search(String keyword) {
    LOGGER.info("Searching study index for keyword: {}", keyword);
    SearchHits<ElasticsearchStudyDocument> hits = studyIndexRepository.findDocumentsByKeyword(keyword);
    return StudySearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public StudySearchHits<ElasticsearchStudyDocument> search(String keyword, String field) {
    LOGGER.info("Searching study index for keyword: {}  field: {}", keyword, field);
    SearchHits<ElasticsearchStudyDocument> hits =
        studyIndexRepository.findDocumentsByKeywordAndField(keyword, Arrays.asList(field));
    return StudySearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public void indexStudy(Study study) {
    ElasticsearchStudyDocument document = documentMapper.fromStudy(study);
    studyIndexRepository.save(document);
  }

  @Override
  public void indexStudies(Collection<Study> studies) {
    for (Study study: studies) {
      this.indexStudy(study);
    }
  }
}
