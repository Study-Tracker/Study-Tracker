package io.studytracker.mapstruct.dto.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordFormDto {

  private Long id;
  @NotBlank private String keyword;
  @NotNull private KeywordCategoryFormDto category;
}
