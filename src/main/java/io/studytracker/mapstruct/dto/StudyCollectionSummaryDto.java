package io.studytracker.mapstruct.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyCollectionSummaryDto {

  private Long id;
  private @NotNull String name;
  private String description;
  private boolean shared;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private Set<StudySlimDto> studies = new HashSet<>();
}
