package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.CustomEntityFieldType;
import lombok.Data;

@Data
public class IntegrationConfigurationSchemaFieldDetailsDto {

  private Long id;
  private String displayName;
  private String fieldName;
  private CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;
  private Integer fieldOrder;

}
