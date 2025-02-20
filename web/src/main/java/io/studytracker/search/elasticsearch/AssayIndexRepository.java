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
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AssayIndexRepository
    extends ElasticsearchRepository<ElasticsearchAssayDocument, Long> {

  @Highlight(
      fields = {
          @HighlightField(name = "tasks.label"),
          @HighlightField(name = "name"),
          @HighlightField(name = "study.name"),
          @HighlightField(name = "study.description"),
          @HighlightField(name = "description"),
          @HighlightField(name = "createdBy.displayName"),
          @HighlightField(name = "lastModifiedBy.displayName"),
          @HighlightField(name = "owner.displayName"),
          @HighlightField(name = "users.displayName"),
          @HighlightField(name = "attributes.*"),
          @HighlightField(name = "assayType.name"),
          @HighlightField(name = "fields.*"),
      })
  @Query("{\"multi_match\": {\"query\": \"?0\" }}")
  SearchHits<ElasticsearchAssayDocument> findDocumentsByKeyword(String keyword);

  @Query("{\"multi_match\": {\"query\": \"?0\" }}")
  @Highlight(
      fields = {
          @HighlightField(name = "tasks.label"),
          @HighlightField(name = "name"),
          @HighlightField(name = "study.name"),
          @HighlightField(name = "study.description"),
          @HighlightField(name = "description"),
          @HighlightField(name = "createdBy.displayName"),
          @HighlightField(name = "lastModifiedBy.displayName"),
          @HighlightField(name = "owner.displayName"),
          @HighlightField(name = "users.displayName"),
          @HighlightField(name = "attributes.*"),
          @HighlightField(name = "assayType.name"),
          @HighlightField(name = "fields.*"),
      })
  SearchPage<ElasticsearchAssayDocument> findDocumentsByKeyword(String keyword, Pageable pageable);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(
      fields = {
          @HighlightField(name = "tasks.label"),
          @HighlightField(name = "name"),
          @HighlightField(name = "study.name"),
          @HighlightField(name = "study.description"),
          @HighlightField(name = "description"),
          @HighlightField(name = "createdBy.displayName"),
          @HighlightField(name = "lastModifiedBy.displayName"),
          @HighlightField(name = "owner.displayName"),
          @HighlightField(name = "users.displayName"),
          @HighlightField(name = "attributes.*"),
          @HighlightField(name = "assayType.name"),
          @HighlightField(name = "fields.*"),
      })
  SearchHits<ElasticsearchAssayDocument> findDocumentsByKeywordAndField(
      String keyword, Collection<String> fields);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(
      fields = {
          @HighlightField(name = "tasks.label"),
          @HighlightField(name = "name"),
          @HighlightField(name = "study.name"),
          @HighlightField(name = "study.description"),
          @HighlightField(name = "description"),
          @HighlightField(name = "createdBy.displayName"),
          @HighlightField(name = "lastModifiedBy.displayName"),
          @HighlightField(name = "owner.displayName"),
          @HighlightField(name = "users.displayName"),
          @HighlightField(name = "attributes.*"),
          @HighlightField(name = "assayType.name"),
          @HighlightField(name = "fields.*"),
      })
  SearchPage<ElasticsearchAssayDocument> findDocumentsByKeywordAndField(
      String keyword, Collection<String> fields, Pageable pageable);

}
