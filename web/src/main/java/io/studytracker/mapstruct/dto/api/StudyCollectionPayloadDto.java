package io.studytracker.mapstruct.dto.api;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyCollectionPayloadDto {
  private Long id;
  private @NotNull String name;
  private String description;
  private boolean shared;
  private Set<Long> studies = new HashSet<>();
}
