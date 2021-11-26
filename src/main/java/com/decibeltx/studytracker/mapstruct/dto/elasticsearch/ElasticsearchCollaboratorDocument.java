package com.decibeltx.studytracker.mapstruct.dto.elasticsearch;

import lombok.Data;

@Data
public class ElasticsearchCollaboratorDocument {

  private String label;
  private String organizationName;
  private String organizationLocation;
  private String contactPersonName;
  private String contactEmail;

}
