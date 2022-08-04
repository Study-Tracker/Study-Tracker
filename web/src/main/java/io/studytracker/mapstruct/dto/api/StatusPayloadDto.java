package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.Status;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusPayloadDto {
  @NotNull private Status status;
}
