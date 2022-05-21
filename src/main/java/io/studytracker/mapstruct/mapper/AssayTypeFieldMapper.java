package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.AssayTypeFieldDto;
import io.studytracker.model.AssayTypeField;
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
