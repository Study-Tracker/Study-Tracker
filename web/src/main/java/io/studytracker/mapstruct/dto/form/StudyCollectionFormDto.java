package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.StudySlimDto;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyCollectionFormDto {

  private Long id;
  private @NotNull String name;
  private String description;
  private boolean shared;
  private Set<StudySlimDto> studies = new HashSet<>();
}
