package io.studytracker.mapstruct.dto.response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayTypeDetailsDto {

  private Long id;
  private String name;
  private String description;
  private boolean active = true;
  private Set<AssayTypeFieldDetailsDto> fields = new HashSet<>();
  private Set<AssayTypeTaskDetailsDto> tasks = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
}
