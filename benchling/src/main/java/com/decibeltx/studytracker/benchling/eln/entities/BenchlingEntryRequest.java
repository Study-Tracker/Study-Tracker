package com.decibeltx.studytracker.benchling.eln.entities;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class BenchlingEntryRequest {

  private String folderId;

  private String name;

  private String entryTemplateId;

  private String schemaId;

  private Map<String, CustomField> customFields = new LinkedHashMap<>();

  private Map<String, Field> fields = new LinkedHashMap<>();

  public void addCustomField(String key, String value) {
    customFields.put(key, new CustomField(value));
  }

  public void addField(String key, String value) {
    fields.put(key, new Field(value));
  }

  @Data
  public static class CustomField {

    private String value;

    public CustomField() {
    }

    public CustomField(String value) {
      this.value = value;
    }

  }

  @Data
  public static class Field {

    private String value;

    private Field() {
    }

    public Field(String value) {
      this.value = value;
    }
  }

}
