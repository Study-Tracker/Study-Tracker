package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.AssayTypeFieldDto;
import io.studytracker.mapstruct.dto.api.AssayTypeFieldPayloadDto;
import io.studytracker.mapstruct.dto.response.AssayTypeFieldDetailsDto;
import io.studytracker.model.AssayTypeField;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeFieldMapper {

  AssayTypeFieldDetailsDto toDetailsDto(AssayTypeField field);


  List<AssayTypeFieldDetailsDto> toDetailsDtoList(List<AssayTypeField> fields);

  AssayTypeField fromDetailsDto(AssayTypeFieldDetailsDto dto);


  List<AssayTypeField> fromDetailsDtoList(List<AssayTypeFieldDetailsDto> dtos);

  AssayTypeFieldDto toDto(AssayTypeField field);
  List<AssayTypeFieldDto> toDtoList(List<AssayTypeField> fields);

  AssayTypeField fromPayloadDto(AssayTypeFieldPayloadDto dto);

}
