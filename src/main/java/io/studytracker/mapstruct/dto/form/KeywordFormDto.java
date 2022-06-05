package io.studytracker.mapstruct.dto.form;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordFormDto {

  private Long id;
  @NotNull private String keyword;
  @NotNull private KeywordCategoryFormDto category;
}
