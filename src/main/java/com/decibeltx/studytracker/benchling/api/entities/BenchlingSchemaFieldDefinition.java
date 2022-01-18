package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingSchemaFieldDefinition {

  private String id;
  private boolean isRequired;
  private boolean isMulti;
  private String name;
  private String type;
  private BenchlingArchiveRecord archiveRecord;

}
