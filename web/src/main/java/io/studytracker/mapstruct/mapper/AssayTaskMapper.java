package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.AssayTaskDto;
import io.studytracker.mapstruct.dto.api.AssayTaskPayloadDto;
import io.studytracker.mapstruct.dto.form.AssayTaskFormDto;
import io.studytracker.mapstruct.dto.response.AssayTaskDetailsDto;
import io.studytracker.model.AssayTask;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssayTaskMapper {
  AssayTaskDetailsDto toDetailsDto(AssayTask task);

  Set<AssayTaskDetailsDto> toDetailsDtoSet(Set<AssayTask> tasks);

  List<AssayTaskDetailsDto> toDetailsDtoList(List<AssayTask> tasks);

  AssayTask fromDetailsDto(AssayTaskDetailsDto dto);

  AssayTask fromFormDto(AssayTaskFormDto dto);

  @Mapping(target = "assignedTo", source = "assignedTo.id")
  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "assayId", source = "assay.id")
  AssayTaskDto toDto(AssayTask task);
  List<AssayTaskDto> toDtoList(List<AssayTask> tasks);

  @Mapping(target = "assignedTo", ignore = true)
  AssayTask fromPayload(AssayTaskPayloadDto dto);
}
