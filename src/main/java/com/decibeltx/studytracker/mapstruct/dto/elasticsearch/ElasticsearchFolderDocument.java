package com.decibeltx.studytracker.mapstruct.dto.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchFolderDocument {
  private String url;
  private String name;
  private String path;
  private String referenceId;
}
