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

package io.studytracker.search.elasticsearch;

import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchAssayDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchPowerSearchDocument;
import io.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import io.studytracker.mapstruct.mapper.ElasticsearchDocumentMapper;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.search.AssaySearchDocument;
import io.studytracker.search.DocumentType;
import io.studytracker.search.GenericSearchHit;
import io.studytracker.search.GenericSearchHits;
import io.studytracker.search.SearchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;

public class ElasticsearchSearchService implements SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSearchService.class);

  @Autowired
  private StudyIndexRepository studyIndexRepository;

  @Autowired
  private AssayIndexRepository assayIndexRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ElasticsearchDocumentMapper documentMapper;

  @Autowired
  private ElasticsearchOperations elasticsearchOperations;

  private GenericSearchHits<ElasticsearchPowerSearchDocument> searchAllIndexes(Query query) {

    LOGGER.debug("Searching all indexes for query: {}", query);

    // Run the search
    SearchHits<AllDocuments> searchHits = elasticsearchOperations
        .search(query, AllDocuments.class, IndexCoordinates.of("st-*"));
    ElasticsearchConverter converter = elasticsearchOperations.getElasticsearchConverter();

    // Convert the results
    List<GenericSearchHit<ElasticsearchPowerSearchDocument>> genericHits = new ArrayList<>();
    for (SearchHit<AllDocuments> searchHit: searchHits) {
      String indexName = searchHit.getIndex();
      if (indexName != null && Arrays.asList("st-studies", "st-assays").contains(searchHit.getIndex())) {

        // Create the document
        ElasticsearchPowerSearchDocument document = new ElasticsearchPowerSearchDocument();
        Document doc  = Document.from(searchHit.getContent());
        if (indexName.equals("st-studies")) {
          ElasticsearchStudyDocument studyDocument = converter.read(ElasticsearchStudyDocument.class, doc);
          document.setData(studyDocument);
          document.setType(DocumentType.STUDY);
          document.setId(studyDocument.getId());
        } else if (indexName.equals("st-assays")) {
          ElasticsearchAssayDocument assayDocument = converter.read(ElasticsearchAssayDocument.class, doc);
          document.setData(assayDocument);
          document.setType(DocumentType.ASSAY);
          document.setId(assayDocument.getId());
        }
        document.setIndex(indexName);

        // Wrap it as a generic search hit
        GenericSearchHit<ElasticsearchPowerSearchDocument> hit = new GenericSearchHit<>();
        hit.setDocument(document);
        hit.setScore(searchHit.getScore());
        hit.setHighlightFields(searchHit.getHighlightFields());

        genericHits.add(hit);

      }
    }

    // Create the return object
    GenericSearchHits<ElasticsearchPowerSearchDocument> genericSearchHits = new GenericSearchHits<>();
    genericSearchHits.setNumHits(searchHits.getTotalHits());
    genericSearchHits.setMaxScore(searchHits.getMaxScore());
    genericSearchHits.setHits(genericHits);

    LOGGER.debug("Found {} hits: {}", genericSearchHits.getNumHits(), genericSearchHits);

    return genericSearchHits;

  }

  @Override
  public GenericSearchHits<ElasticsearchPowerSearchDocument> search(String keyword) {
    LOGGER.debug("Searching for keyword: {}", keyword);
    Query query = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.multiMatchQuery(keyword))
        .withHighlightFields(new HighlightBuilder.Field("*"))
        .build();
    return searchAllIndexes(query);
  }

  @Override
  public GenericSearchHits<ElasticsearchPowerSearchDocument> search(String keyword, String field) {
    LOGGER.debug("Searching for keyword: {}, field: {}", keyword, field);
    Query query = new NativeSearchQueryBuilder()
        .withQuery(QueryBuilders.multiMatchQuery(keyword))
        .withHighlightFields(new HighlightBuilder.Field("*"))
        .withFields(field)
        .build();
    return searchAllIndexes(query);
  }

  @Override
  public GenericSearchHits<ElasticsearchStudyDocument> searchStudies(String keyword) {
    LOGGER.info("Searching study index for keyword: {}", keyword);
    SearchHits<ElasticsearchStudyDocument> hits =
        studyIndexRepository.findDocumentsByKeyword(keyword);
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public GenericSearchHits<ElasticsearchStudyDocument> searchStudies(String keyword, String field) {
    LOGGER.info("Searching study index for keyword: {}  field: {}", keyword, field);
    SearchHits<ElasticsearchStudyDocument> hits =
        studyIndexRepository.findDocumentsByKeywordAndField(keyword, Arrays.asList(field));
    return GenericSearchHits.fromElasticsearchHits(hits);
  }


  @Override
  public GenericSearchHits<ElasticsearchAssayDocument> searchAssays(String keyword) {
    LOGGER.info("Searching assay index for keyword: {}", keyword);
    SearchHits<ElasticsearchAssayDocument> hits =
        assayIndexRepository.findDocumentsByKeyword(keyword);
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword, String field) {
    LOGGER.info("Searching assay index for keyword: {}  field: {}", keyword, field);
    SearchHits<ElasticsearchAssayDocument> hits =
        assayIndexRepository.findDocumentsByKeywordAndField(keyword, Arrays.asList(field));
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public void indexStudy(Study study) {
    ElasticsearchStudyDocument document = documentMapper.fromStudy(study);
    studyIndexRepository.save(document);
  }

  @Override
  public void indexStudies(Collection<Study> studies) {
    studies.forEach(this::indexStudy);
  }


  @Override
  public void indexAssay(Assay assay) {
    ElasticsearchAssayDocument document = documentMapper.fromAssay(assay);
    assayIndexRepository.save(document);
  }

  @Override
  public void indexAssays(Collection<Assay> assays) {
    assays.forEach(this::indexAssay);
  }

}
