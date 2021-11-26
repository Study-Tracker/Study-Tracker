package com.decibeltx.studytracker.search.elasticsearch;

import com.decibeltx.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StudyIndexRepository
    extends ElasticsearchRepository<ElasticsearchStudyDocument, Long> {

  @Highlight(fields = {
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
  SearchHits<ElasticsearchStudyDocument> findDocumentsByKeyword(String keyword);

  @Query("{\"multi_match\": {\"query\": \"?0\" }}")
  @Highlight(fields = {
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
  SearchPage<ElasticsearchStudyDocument> findDocumentsByKeyword(String keyword, Pageable pageable);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(fields = {
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
  SearchHits<ElasticsearchStudyDocument> findDocumentsByKeywordAndField(String keyword, Collection<String> fields);

  @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": \"?1\" }}")
  @Highlight(fields = {
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
  SearchPage<ElasticsearchStudyDocument> findDocumentsByKeywordAndField(String keyword, Collection<String> fields, Pageable pageable);

}
