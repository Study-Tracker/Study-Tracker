package io.studytracker.mapstruct.dto;

import io.studytracker.model.NotebookEntryTemplate;
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
