package io.studytracker.mapstruct.dto.response;

import lombok.Data;

@Data
public class KeywordDetailsDto {

  private Long id;
  private String keyword;
  private KeywordCategoryDetailsDto category;
}
