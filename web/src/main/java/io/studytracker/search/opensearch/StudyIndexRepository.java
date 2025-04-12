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

import io.studytracker.mapstruct.dto.opensearch.OpensearchStudyDocument;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StudyIndexRepository
    extends ElasticsearchRepository<OpensearchStudyDocument, Long> {

  @Highlight(
      fields = {
        @HighlightField(name = "keywords.keyword"),
        @HighlightField(name = "name"),
        @HighlightField(name = "program.name"),
        @HighlightField(name = "program.description"),
        @HighlightField(name = "description"),
        @HighlightField(name = "collaborator.label"),
        @HighlightField(name = "collaborator.organizationName"),
        @HighlightField(name = "createdBy.displayName"),
        @HighlightField(name = "lastModifiedBy.displayName"),
        @HighlightField(name = "owner.displayName"),
        @HighlightField(name = "users.displayName"),
        @HighlightField(name = "attributes.*"),
        @HighlightField(name = "externalLinks.label"),
        @HighlightField(name = "comments.text"),
        @HighlightField(name = "comments.createdBy.displayName"),
        @HighlightField(name = "conclusions.content")
      })
  @Query("{\"multi_match\": {\"query\": \"?0\" }}")
  SearchHits<OpensearchStudyDocument> findDocumentsByKeyword(String keyword);

  @Query("{\"multi_match\": {\"query\": \"?0\" }}")
  @Highlight(
      fields = {
        @HighlightField(name = "keywords.keyword"),
        @HighlightField(name = "name"),
        @HighlightField(name = "program.name"),
        @HighlightField(name = "program.description"),
        @HighlightField(name = "description"),
        @HighlightField(name = "collaborator.label"),
        @HighlightField(name = "collaborator.organizationName"),
        @HighlightField(name = "createdBy.displayName"),
        @HighlightField(name = "lastModifiedBy.displayName"),
        @HighlightField(name = "owner.displayName"),
        @HighlightField(name = "users.displayName"),
        @HighlightField(name = "attributes.*"),
        @HighlightField(name = "externalLinks.label"),
        @HighlightField(name = "comments.text"),
        @HighlightField(name = "comments.createdBy.displayName"),
        @HighlightField(name = "conclusions.content")
      })
  SearchPage<OpensearchStudyDocument> findDocumentsByKeyword(String keyword, Pageable pageable);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(
      fields = {
        @HighlightField(name = "keywords.keyword"),
        @HighlightField(name = "name"),
        @HighlightField(name = "program.name"),
        @HighlightField(name = "program.description"),
        @HighlightField(name = "description"),
        @HighlightField(name = "collaborator.label"),
        @HighlightField(name = "collaborator.organizationName"),
        @HighlightField(name = "createdBy.displayName"),
        @HighlightField(name = "lastModifiedBy.displayName"),
        @HighlightField(name = "owner.displayName"),
        @HighlightField(name = "users.displayName"),
        @HighlightField(name = "attributes.*"),
        @HighlightField(name = "externalLinks.label"),
        @HighlightField(name = "comments.text"),
        @HighlightField(name = "comments.createdBy.displayName"),
        @HighlightField(name = "conclusions.content")
      })
  SearchHits<OpensearchStudyDocument> findDocumentsByKeywordAndField(
      String keyword, Collection<String> fields);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(
      fields = {
        @HighlightField(name = "keywords.keyword"),
        @HighlightField(name = "name"),
        @HighlightField(name = "program.name"),
        @HighlightField(name = "program.description"),
        @HighlightField(name = "description"),
        @HighlightField(name = "collaborator.label"),
        @HighlightField(name = "collaborator.organizationName"),
        @HighlightField(name = "createdBy.displayName"),
        @HighlightField(name = "lastModifiedBy.displayName"),
        @HighlightField(name = "owner.displayName"),
        @HighlightField(name = "users.displayName"),
        @HighlightField(name = "attributes.*"),
        @HighlightField(name = "externalLinks.label"),
        @HighlightField(name = "comments.text"),
        @HighlightField(name = "comments.createdBy.displayName"),
        @HighlightField(name = "conclusions.content")
      })
  SearchPage<OpensearchStudyDocument> findDocumentsByKeywordAndField(
      String keyword, Collection<String> fields, Pageable pageable);
}
