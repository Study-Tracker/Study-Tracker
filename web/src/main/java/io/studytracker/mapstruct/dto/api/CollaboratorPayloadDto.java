package io.studytracker.mapstruct.dto.api;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollaboratorPayloadDto {

  private Long id;

  @NotNull private String label;

  @NotNull private String organizationName;

  private String organizationLocation;

  private String contactPersonName;

  private String contactEmail;

  @NotNull private String code;

  private boolean active = true;
}
