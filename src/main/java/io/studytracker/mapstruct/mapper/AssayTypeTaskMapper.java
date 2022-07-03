package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.AssayTypeTaskDto;
import io.studytracker.model.AssayTypeTask;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeTaskMapper {

  AssayTypeTaskDto toDto(AssayTypeTask task);

  Set<AssayTypeTaskDto> toDtoSet(Set<AssayTypeTask> tasks);

  List<AssayTypeTaskDto> toDtoList(List<AssayTypeTask> tasks);

  AssayTypeTask fromDto(AssayTypeTaskDto dto);

  Set<AssayTypeTask> fromDtoSet(Set<AssayTypeTaskDto> dtos);

  List<AssayTypeTask> fromDtoList(List<AssayTypeTaskDto> dtos);
}
