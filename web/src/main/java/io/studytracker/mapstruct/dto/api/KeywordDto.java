package io.studytracker.mapstruct.dto.api;

import lombok.Data;

@Data
public class KeywordDto {
  private Long id;
  private String keyword;
  private Long categoryId;
}
