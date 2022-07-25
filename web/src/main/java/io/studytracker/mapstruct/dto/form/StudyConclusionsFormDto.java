package io.studytracker.mapstruct.dto.form;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudyConclusionsFormDto {

  private Long id;
  private @NotBlank String content;

}
