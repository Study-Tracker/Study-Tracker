package io.studytracker.mapstruct.dto.api;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordPayloadDto {
  private Long id;
  @NotBlank private String keyword;
  @NotNull Long categoryId;
}
