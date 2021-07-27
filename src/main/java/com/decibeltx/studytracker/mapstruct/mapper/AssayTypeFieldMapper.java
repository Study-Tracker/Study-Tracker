package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.AssayTypeFieldDto;
import com.decibeltx.studytracker.model.AssayTypeField;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeFieldMapper {

  AssayTypeFieldDto toDto(AssayTypeField field);
  Set<AssayTypeFieldDto> toDtoSet(Set<AssayTypeField> fields);
  List<AssayTypeFieldDto> toDtoList(List<AssayTypeField> fields);
  AssayTypeField fromDto(AssayTypeFieldDto dto);
  Set<AssayTypeField> fromDtoSet(Set<AssayTypeFieldDto> dtos);
  List<AssayTypeField> fromDtoList(List<AssayTypeFieldDto> dtos);

}
