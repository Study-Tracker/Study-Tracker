package io.studytracker.mapstruct.dto.api;

import lombok.Data;

@Data
public class CollaboratorDto {
  private Long id;
  private String label;
  private String organizationName;
  private String organizationLocation;
  private String contactPersonName;
  private String contactEmail;
  private String code;
  private boolean active = true;
}
