package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingEntryTemplateList {

  private List<BenchlingEntryTemplate> entryTemplates = new ArrayList<>();
  private String nextToken;

}
