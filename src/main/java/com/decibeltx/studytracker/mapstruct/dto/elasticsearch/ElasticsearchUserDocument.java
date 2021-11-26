package com.decibeltx.studytracker.mapstruct.dto.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchUserDocument {

  private String username;
  private String displayName;
  private String email;

}
