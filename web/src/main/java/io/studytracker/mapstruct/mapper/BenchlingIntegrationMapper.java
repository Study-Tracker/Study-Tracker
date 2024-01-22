package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.BenchlingIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.BenchlingIntegrationDetailsDto;
import io.studytracker.model.BenchlingIntegration;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface BenchlingIntegrationMapper {
    
    BenchlingIntegrationDetailsDto toDetailsDto(BenchlingIntegration integration);
    Set<BenchlingIntegrationDetailsDto> toDetailsDto(Set<BenchlingIntegration> integrations);
    List<BenchlingIntegrationDetailsDto> toDetailsDto(List<BenchlingIntegration> integrations);
    
    BenchlingIntegration fromFormDto(BenchlingIntegrationFormDto dto);
    
}
