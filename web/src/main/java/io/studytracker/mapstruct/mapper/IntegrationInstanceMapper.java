package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.IntegrationInstanceDetailsDto;
import io.studytracker.model.IntegrationInstance;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntegrationInstanceMapper {

  IntegrationInstanceDetailsDto toDetails(IntegrationInstance integrationInstance);
  Set<IntegrationInstanceDetailsDto> toDetailsSet(Set<IntegrationInstance> integrationInstances);
  List<IntegrationInstanceDetailsDto> toDetailsList(List<IntegrationInstance> integrationInstances);

}
