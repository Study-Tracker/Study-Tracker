package com.decibeltx.studytracker.search;

import com.decibeltx.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;

@Data
public class StudySearchHit<T extends StudySearchDocument<?>> {

  private T document;
  private Float score;
  private Map<String, List<String>> highlightFields = new LinkedHashMap<>();

  public static StudySearchHit<ElasticsearchStudyDocument> fromElasticsearchSearchHit(SearchHit<ElasticsearchStudyDocument> esHit) {
    StudySearchHit<ElasticsearchStudyDocument> hit = new StudySearchHit<>();
    hit.setDocument(esHit.getContent());
    hit.setScore(esHit.getScore());
    hit.setHighlightFields(esHit.getHighlightFields());
    return hit;
  }

}
