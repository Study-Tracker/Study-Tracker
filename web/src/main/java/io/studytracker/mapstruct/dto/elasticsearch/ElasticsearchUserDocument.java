package io.studytracker.mapstruct.dto.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchUserDocument {

  private String displayName;
  private String email;
}
