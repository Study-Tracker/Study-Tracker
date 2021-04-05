package com.decibeltx.studytracker.benchling.eln.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingFolder {

  private BenchlingArchiveRecord archiveRecord;

  private String id;

  private String name;

  private String parentFolderId;

  private String projectId;

}
