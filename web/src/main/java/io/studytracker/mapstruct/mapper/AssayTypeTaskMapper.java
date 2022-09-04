package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.AssayTypeTaskDto;
import io.studytracker.mapstruct.dto.api.AssayTypeTaskPayloadDto;
import io.studytracker.mapstruct.dto.response.AssayTypeTaskDetailsDto;
import io.studytracker.model.AssayTypeTask;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeTaskMapper {

  AssayTypeTaskDetailsDto toDetailsDto(AssayTypeTask task);

  List<AssayTypeTaskDetailsDto> toDetailsDtoList(List<AssayTypeTask> tasks);

  AssayTypeTask fromDetailsDto(AssayTypeTaskDetailsDto dto);

  AssayTypeTaskDto toDto(AssayTypeTask task);
  List<AssayTypeTaskDto> toDtoList(List<AssayTypeTask> tasks);
  AssayTypeTask fromPayloadDto(AssayTypeTaskPayloadDto dto);

}
