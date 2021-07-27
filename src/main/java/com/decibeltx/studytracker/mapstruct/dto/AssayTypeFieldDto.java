package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.CustomEntityFieldType;
import lombok.Data;

@Data
public class AssayTypeFieldDto {

  private Long id;
  private String displayName;
  private String fieldName;
  private CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;

}
