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
