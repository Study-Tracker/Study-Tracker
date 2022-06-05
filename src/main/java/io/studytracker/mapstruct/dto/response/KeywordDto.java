package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class KeywordDto {

  private Long id;
  private String keyword;
  private KeywordCategoryDto category;
}
