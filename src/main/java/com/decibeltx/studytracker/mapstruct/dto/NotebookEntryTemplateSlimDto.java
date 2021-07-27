package com.decibeltx.studytracker.mapstruct.dto;

import lombok.Data;

@Data
public class NotebookEntryTemplateSlimDto {

  private Long id;
  private String name;
  private String templateId;
  private boolean active;

}
