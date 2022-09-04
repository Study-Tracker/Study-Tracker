package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.AssayTypeFieldDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayTypeTaskDetailsDto;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeFormDto {

  private Long id;
  @NotNull private String name;
  @NotNull private String description;
  private boolean active = true;
  private Set<AssayTypeFieldDetailsDto> fields = new HashSet<>();
  private Set<AssayTypeTaskDetailsDto> tasks = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
}
