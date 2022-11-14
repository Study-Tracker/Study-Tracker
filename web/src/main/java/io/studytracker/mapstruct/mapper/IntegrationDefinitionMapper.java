package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.IntegrationDefinitionDetailsDto;
import io.studytracker.model.IntegrationDefinition;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationDefinitionMapper {

  IntegrationDefinitionDetailsDto toDetails(IntegrationDefinition integrationDefinition);
  Set<IntegrationDefinitionDetailsDto> toDetailsSet(Set<IntegrationDefinition> integrationDefinitions);
  List<IntegrationDefinitionDetailsDto> toDetailsList(List<IntegrationDefinition> integrationDefinitions);

}
