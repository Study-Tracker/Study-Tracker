package io.studytracker.mapstruct.dto.api;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class StudyCollectionDto {

  private Long id;
  private String name;
  private String description;
  private boolean shared;
  private Long createdBy;
  private Long lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private Set<Long> studies = new HashSet<>();
}
