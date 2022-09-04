package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.TaskStatus;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeTaskPayloadDto {
  private Long id;
  @NotNull private TaskStatus status;
  @NotNull private String label;
  private Integer order;
}
