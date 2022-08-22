package io.studytracker.search;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

@Data
public class GenericSearchHits<T extends SearchDocument<?>> {

  private Long numHits;
  private Float maxScore;
  private List<GenericSearchHit<T>> hits = new ArrayList<>();

  public static <S extends SearchDocument<?>> GenericSearchHits<S> fromElasticsearchHits(
      SearchHits<S> searchHits) {
    GenericSearchHits<S> genericHits = new GenericSearchHits<>();
    genericHits.setNumHits(searchHits.getTotalHits());
    genericHits.setMaxScore(searchHits.getMaxScore());
    List<GenericSearchHit<S>> hits = new ArrayList<>();
    for (SearchHit<S> searchHit : searchHits.getSearchHits()) {
      hits.add(GenericSearchHit.fromElasticsearchSearchHit(searchHit));
    }
    genericHits.setHits(hits);
    return genericHits;
  }
}
