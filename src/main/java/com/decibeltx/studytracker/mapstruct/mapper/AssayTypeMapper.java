package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.AssayTypeDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.AssayTypeSlimDto;
import com.decibeltx.studytracker.model.AssayType;
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

}
