package io.studytracker.mapstruct.dto.elasticsearch;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class ElasticsearchAssayTypeDocument {

  private Long id;
  private String name;
  private Map<String, Object> attributes = new LinkedHashMap<>();

}
