package com.decibeltx.studytracker.mapstruct.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeSlimDto {

  private Long id;
  @NotNull private String name;
  @NotNull private String description;
  private boolean active = true;

}
