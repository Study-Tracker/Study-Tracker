package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingSchemaFieldValue {

  private String displayValue;
  private boolean isMulti;
  private String textValue;
  private String type;
  private String value;

}
