package io.studytracker.mapstruct.dto;

import io.studytracker.model.CustomEntityFieldType;
import lombok.Data;

@Data
public class AssayTypeFieldDto {

  private Long id;
  private String displayName;
  private String fieldName;
  private CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;

}
