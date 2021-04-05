package com.decibeltx.studytracker.model;

import lombok.Data;

@Data
public class AssayTypeField {

  private String displayName;

  private String fieldName;

  private AssayFieldType type;

  private boolean required;

  private String description;

  public AssayTypeField() {
  }

  public AssayTypeField(String displayName, String fieldName,
      AssayFieldType type) {
    this.displayName = displayName;
    this.fieldName = fieldName;
    this.type = type;
    this.required = false;
  }

  public AssayTypeField(String displayName, String fieldName,
      AssayFieldType type, boolean required) {
    this.displayName = displayName;
    this.fieldName = fieldName;
    this.type = type;
    this.required = required;
  }

  public AssayTypeField(String displayName, String fieldName,
      AssayFieldType type, boolean required, String description) {
    this.displayName = displayName;
    this.fieldName = fieldName;
    this.type = type;
    this.required = required;
    this.description = description;
  }

  public enum AssayFieldType {
    STRING,
    TEXT,
    INTEGER,
    FLOAT,
    DATE,
    BOOLEAN
  }

}
