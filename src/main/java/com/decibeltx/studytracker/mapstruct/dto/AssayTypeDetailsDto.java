package com.decibeltx.studytracker.mapstruct.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeDetailsDto {

  private Long id;
  @NotNull private String name;
  @NotNull private String description;
  private boolean active = true;
  private Set<AssayTypeFieldDto> fields = new HashSet<>();
  private Set<AssayTypeTaskDto> tasks = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();

}
