package io.studytracker.mapstruct.dto.elasticsearch;

import io.studytracker.search.DocumentType;
import io.studytracker.search.PowerSearchDocument;
import lombok.Data;

@Data
public class ElasticsearchPowerSearchDocument implements PowerSearchDocument<Long> {

  private Long id;
  private String index;
  private DocumentType type;
  private Object data;

}
