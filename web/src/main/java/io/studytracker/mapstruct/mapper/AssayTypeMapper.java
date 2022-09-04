package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.AssayTypeDto;
import io.studytracker.mapstruct.dto.api.AssayTypePayloadDto;
import io.studytracker.mapstruct.dto.form.AssayTypeFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayTypeSlimDto;
import io.studytracker.model.AssayType;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeMapper {

  AssayTypeDetailsDto toDetailsDto(AssayType assayType);

  List<AssayTypeDetailsDto> toDetailsDtoList(List<AssayType> assayTypes);

  AssayType fromDetailsDto(AssayTypeDetailsDto dto);

  List<AssayType> fromDetailsDtoList(List<AssayTypeDetailsDto> dtos);

  AssayTypeSlimDto toSlimDto(AssayType assayType);

  List<AssayTypeSlimDto> toSlimDtoList(List<AssayType> assayTypes);

  AssayType fromSlimDto(AssayTypeSlimDto dto);

  List<AssayType> fromSlimDtoList(List<AssayTypeSlimDto> dtos);

  AssayType fromFormDto(AssayTypeFormDto dto);

  AssayType fromPayloadDto(AssayTypePayloadDto dto);

  AssayTypeDto toDto(AssayType assayType);
  List<AssayTypeDto> toDtoList(List<AssayType> assayTypes);
}
