package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingEntryTemplate {

  private String id;
  private String webUrl;
  private String apiUrl;
  private Date createdAt;
  private Date modifiedAt;
  private BenchlingUser creator;
  private String name;
  private Map<String, BenchlingSchemaFieldValue> fields = new LinkedHashMap<>();
  private Map<String, BenchlingCustomField> customFields = new LinkedHashMap<>();
  private BenchlingEntrySchema schema;
  private String templateCollectionId;

}
