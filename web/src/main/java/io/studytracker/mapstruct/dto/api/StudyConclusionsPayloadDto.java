package io.studytracker.mapstruct.dto.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyConclusionsPayloadDto {

  private Long id;
  private @NotNull Long studyId;
  private @NotBlank String content;

}
