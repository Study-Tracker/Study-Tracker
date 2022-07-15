package io.studytracker.mapstruct.dto.form;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyConclusionsFormDto {

  private Long id;
  private @NotNull String content;

}
