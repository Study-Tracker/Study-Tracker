package io.studytracker.mapstruct.dto.elasticsearch;

import io.studytracker.model.Status;
import io.studytracker.search.StudySearchDocument;
import lombok.Data;

@Data
public class ElasticsearchStudySummaryDocument implements StudySearchDocument<Long> {

  private Long id;
  private String code;
  private String externalCode;
  private Status status;
  private String name;
  private String description;

}
