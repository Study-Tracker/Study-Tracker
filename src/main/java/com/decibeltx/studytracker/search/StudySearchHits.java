package com.decibeltx.studytracker.search;

import com.decibeltx.studytracker.mapstruct.dto.elasticsearch.ElasticsearchStudyDocument;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

@Data
public class StudySearchHits<T extends StudySearchDocument<?>> {

  private Long numHits;
  private Float maxScore;
  private List<StudySearchHit<T>> hits = new ArrayList<>();

  public static StudySearchHits<ElasticsearchStudyDocument> fromElasticsearchHits(SearchHits<ElasticsearchStudyDocument> searchHits) {
    StudySearchHits<ElasticsearchStudyDocument> studySearchHits = new StudySearchHits<>();
    studySearchHits.setNumHits(searchHits.getTotalHits());
    studySearchHits.setMaxScore(searchHits.getMaxScore());
    List<StudySearchHit<ElasticsearchStudyDocument>> hits = new ArrayList<>();
    for (SearchHit<ElasticsearchStudyDocument> searchHit: searchHits.getSearchHits()) {
      hits.add(StudySearchHit.fromElasticsearchSearchHit(searchHit));
    }
    studySearchHits.setHits(hits);
    return studySearchHits;
  }

}
