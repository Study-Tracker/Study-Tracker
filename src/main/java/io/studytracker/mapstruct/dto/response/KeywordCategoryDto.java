package io.studytracker.mapstruct.dto.response;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordCategoryDto {

  private Long id;
  @NotNull private String name;
}
