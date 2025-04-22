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

package io.studytracker.search.opensearch;

import io.studytracker.mapstruct.dto.opensearch.OpensearchAssayDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchPowerSearchDocument;
import io.studytracker.mapstruct.dto.opensearch.OpensearchStudyDocument;
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
import org.opensearch.data.client.osc.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;

public class OpensearchSearchService implements SearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpensearchSearchService.class);

  @Autowired
  private StudyIndexRepository studyIndexRepository;

  @Autowired
  private AssayIndexRepository assayIndexRepository;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private ElasticsearchDocumentMapper documentMapper;

  @Autowired
  private ElasticsearchOperations elasticsearchOperations;
  
  private HighlightQuery getHighlightQuery() {
    Highlight highlight = new Highlight(List.of(new HighlightField("*")));
    return new HighlightQuery(highlight, String.class);
  }

  private GenericSearchHits<OpensearchPowerSearchDocument> searchAllIndexes(Query query) {

    LOGGER.debug("Searching all indexes for query: {}", query);

    // Run the search
    SearchHits<AllDocuments> searchHits = elasticsearchOperations
        .search(query, AllDocuments.class, IndexCoordinates.of("st-*"));
    ElasticsearchConverter converter = elasticsearchOperations.getElasticsearchConverter();

    // Convert the results
    List<GenericSearchHit<OpensearchPowerSearchDocument>> genericHits = new ArrayList<>();
    for (SearchHit<AllDocuments> searchHit: searchHits) {
      String indexName = searchHit.getIndex();
      if (indexName != null && Arrays.asList("st-studies", "st-assays").contains(searchHit.getIndex())) {

        // Create the document
        OpensearchPowerSearchDocument document = new OpensearchPowerSearchDocument();
        Document doc  = Document.from(searchHit.getContent());
        if (indexName.equals("st-studies")) {
          OpensearchStudyDocument studyDocument = converter.read(OpensearchStudyDocument.class, doc);
          document.setData(studyDocument);
          document.setType(DocumentType.STUDY);
          document.setId(studyDocument.getId());
        } else if (indexName.equals("st-assays")) {
          OpensearchAssayDocument assayDocument = converter.read(OpensearchAssayDocument.class, doc);
          document.setData(assayDocument);
          document.setType(DocumentType.ASSAY);
          document.setId(assayDocument.getId());
        }
        document.setIndex(indexName);

        // Wrap it as a generic search hit
        GenericSearchHit<OpensearchPowerSearchDocument> hit = new GenericSearchHit<>();
        hit.setDocument(document);
        hit.setScore(searchHit.getScore());
        hit.setHighlightFields(searchHit.getHighlightFields());

        genericHits.add(hit);

      }
    }

    // Create the return object
    GenericSearchHits<OpensearchPowerSearchDocument> genericSearchHits = new GenericSearchHits<>();
    genericSearchHits.setNumHits(searchHits.getTotalHits());
    genericSearchHits.setMaxScore(searchHits.getMaxScore());
    genericSearchHits.setHits(genericHits);

    LOGGER.debug("Found {} hits: {}", genericSearchHits.getNumHits(), genericSearchHits);

    return genericSearchHits;

  }

  @Override
  public GenericSearchHits<OpensearchPowerSearchDocument> search(String keyword) {
    LOGGER.debug("Searching for keyword: {}", keyword);
    
    Query query = NativeQuery.builder()
        .withQuery(q -> q.multiMatch(m -> m.query(keyword)))
        .withHighlightQuery(getHighlightQuery())
        .build();
    return searchAllIndexes(query);
  }

  @Override
  public GenericSearchHits<OpensearchPowerSearchDocument> search(String keyword, String field) {
    LOGGER.debug("Searching for keyword: {}, field: {}", keyword, field);
    Query query = NativeQuery.builder()
        .withQuery(q -> q.multiMatch(m -> m.query(keyword)))
        .withHighlightQuery(getHighlightQuery())
        .withFields(field)
        .build();
    return searchAllIndexes(query);
  }

  @Override
  public GenericSearchHits<OpensearchStudyDocument> searchStudies(String keyword) {
    LOGGER.info("Searching study index for keyword: {}", keyword);
    SearchHits<OpensearchStudyDocument> hits =
        studyIndexRepository.findDocumentsByKeyword(keyword);
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public GenericSearchHits<OpensearchStudyDocument> searchStudies(String keyword, String field) {
    LOGGER.info("Searching study index for keyword: {}  field: {}", keyword, field);
    SearchHits<OpensearchStudyDocument> hits =
        studyIndexRepository.findDocumentsByKeywordAndField(keyword, Arrays.asList(field));
    return GenericSearchHits.fromElasticsearchHits(hits);
  }


  @Override
  public GenericSearchHits<OpensearchAssayDocument> searchAssays(String keyword) {
    LOGGER.info("Searching assay index for keyword: {}", keyword);
    SearchHits<OpensearchAssayDocument> hits =
        assayIndexRepository.findDocumentsByKeyword(keyword);
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword, String field) {
    LOGGER.info("Searching assay index for keyword: {}  field: {}", keyword, field);
    SearchHits<OpensearchAssayDocument> hits =
        assayIndexRepository.findDocumentsByKeywordAndField(keyword, Arrays.asList(field));
    return GenericSearchHits.fromElasticsearchHits(hits);
  }

  @Override
  public void indexStudy(Study study) {
    OpensearchStudyDocument document = documentMapper.fromStudy(study);
    studyIndexRepository.save(document);
  }

  @Override
  public void indexStudies(Collection<Study> studies) {
    studies.forEach(this::indexStudy);
  }


  @Override
  public void indexAssay(Assay assay) {
    OpensearchAssayDocument document = documentMapper.fromAssay(assay);
    assayIndexRepository.save(document);
  }

  @Override
  public void indexAssays(Collection<Assay> assays) {
    assays.forEach(this::indexAssay);
  }

}
