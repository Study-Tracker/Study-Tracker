package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.Status;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssaySlimDto {

  private Long id;
  private Status status;
  private AssayTypeSlimDto assayType;
  @NotNull private String name;
  private String code;
  private boolean active = true;
}
