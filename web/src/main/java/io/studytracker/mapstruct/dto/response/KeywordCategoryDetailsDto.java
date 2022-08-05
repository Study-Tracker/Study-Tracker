package io.studytracker.mapstruct.dto.response;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordCategoryDetailsDto {

  private Long id;
  @NotNull private String name;
}
