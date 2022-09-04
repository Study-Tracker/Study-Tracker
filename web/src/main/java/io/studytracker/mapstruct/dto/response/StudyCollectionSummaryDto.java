package io.studytracker.mapstruct.dto.response;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class StudyCollectionSummaryDto {

  private Long id;
  private String name;
  private String description;
  private boolean shared;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private Set<StudySlimDto> studies = new HashSet<>();
}
