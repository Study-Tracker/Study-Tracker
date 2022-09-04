package io.studytracker.mapstruct.dto.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypePayloadDto {
  private Long id;
  @NotNull private String name;
  @NotNull private String description;
  private boolean active = true;
  private Set<AssayTypeFieldPayloadDto> fields = new HashSet<>();
  private Set<AssayTypeTaskPayloadDto> tasks = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
}
