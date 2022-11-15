package io.studytracker.mapstruct.dto.response;

import io.studytracker.integration.IntegrationType;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Data
public class IntegrationDefinitionDetailsDto {

  private Long id;
  private IntegrationType type;
  private boolean active;
  private Integer version;
  private Set<IntegrationConfigurationSchemaFieldDetailsDto> configurationSchemaFields = new HashSet<>();

}
