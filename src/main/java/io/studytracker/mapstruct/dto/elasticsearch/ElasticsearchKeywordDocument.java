package io.studytracker.mapstruct.dto.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchKeywordDocument {
  private String keyword;
  private String category;
}
