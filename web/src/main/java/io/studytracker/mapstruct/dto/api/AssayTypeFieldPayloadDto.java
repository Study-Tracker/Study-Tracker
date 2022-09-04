package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.CustomEntityFieldType;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeFieldPayloadDto {
  private Long id;
  private @NotNull String displayName;
  private String fieldName;
  private @NotNull CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;
}
