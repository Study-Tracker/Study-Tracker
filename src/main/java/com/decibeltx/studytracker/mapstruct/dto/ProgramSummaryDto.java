package com.decibeltx.studytracker.mapstruct.dto;

import lombok.Data;

@Data
public class ProgramSummaryDto {

  private Long id;
  private String code;
  private String name;
  private String description;
  private boolean active;

}
