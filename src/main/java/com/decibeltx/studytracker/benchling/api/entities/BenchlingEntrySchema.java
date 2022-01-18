package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingEntrySchema {

  private String id;
  private String name;
  private String modifiedAt;
  private List<BenchlingSchemaFieldDefinition> fieldDefinitions = new ArrayList<>();
  private String prefix;
  private String registryId;
  private String type;
  private BenchlingArchiveRecord archiveRecord;

}
