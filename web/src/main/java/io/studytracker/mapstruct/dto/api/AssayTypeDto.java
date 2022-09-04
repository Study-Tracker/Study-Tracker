package io.studytracker.mapstruct.dto.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayTypeDto {
  private Long id;
  private String name;
  private String description;
  private boolean active = true;
  private Set<AssayTypeFieldDto> fields = new HashSet<>();
  private Set<AssayTypeTaskDto> tasks = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
}
