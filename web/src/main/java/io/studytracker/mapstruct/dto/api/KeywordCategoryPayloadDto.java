package io.studytracker.mapstruct.dto.api;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeywordCategoryPayloadDto {
  private Long id;
  private @NotBlank String name;
}
