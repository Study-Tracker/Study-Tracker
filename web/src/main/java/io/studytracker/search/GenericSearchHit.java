package io.studytracker.search;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;

@Data
public class GenericSearchHit<T extends SearchDocument<?>> {

  private T document;
  private Float score;
  private Map<String, List<String>> highlightFields = new LinkedHashMap<>();

  public static <S extends SearchDocument<?>> GenericSearchHit<S> fromElasticsearchSearchHit(
      SearchHit<S> esHit) {
    GenericSearchHit<S> hit = new GenericSearchHit<>();
    hit.setDocument(esHit.getContent());
    hit.setScore(esHit.getScore());
    hit.setHighlightFields(esHit.getHighlightFields());
    return hit;
  }
}
