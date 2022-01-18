package com.decibeltx.studytracker.mapstruct.dto;

import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import java.util.Date;
import lombok.Data;

@Data
public class NotebookEntryTemplateDetailsDto {

  private Long id;
  private String name;
  private String templateId;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active;
  private NotebookEntryTemplate.Category category;
  private boolean isDefault;

}
