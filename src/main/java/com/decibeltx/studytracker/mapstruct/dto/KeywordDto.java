package com.decibeltx.studytracker.mapstruct.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KeywordDto {

  private Long id;
  @NotNull private String keyword;
  @NotNull private String category;

}
