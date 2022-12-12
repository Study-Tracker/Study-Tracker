package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.IntegrationConfigurationSchemaFieldDetailsDto;
import io.studytracker.model.IntegrationConfigurationSchemaField;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationConfigurationSchemaFieldMapper {

  IntegrationConfigurationSchemaFieldDetailsDto toDetails(IntegrationConfigurationSchemaField integrationConfigurationSchemaField);
  Set<IntegrationConfigurationSchemaFieldDetailsDto> toDetailsSet(Set<IntegrationConfigurationSchemaField> integrationConfigurationSchemaFields);
  List<IntegrationConfigurationSchemaFieldDetailsDto> toDetailsList(List<IntegrationConfigurationSchemaField> integrationConfigurationSchemaFields);
  
}
