package io.studytracker.mapstruct.dto.form;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentFormDto {

  private Long id;
  private @NotBlank String text;


}
