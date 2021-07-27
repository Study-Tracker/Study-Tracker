package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.AssayTaskDto;
import com.decibeltx.studytracker.model.AssayTask;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTaskMapper {
  AssayTaskDto toDto(AssayTask task);
  Set<AssayTaskDto> toDtoSet(Set<AssayTask> tasks);
  List<AssayTaskDto> toDtoList(List<AssayTask> tasks);
  AssayTask fromDto(AssayTaskDto dto);
  Set<AssayTask> fromDtoSet(Set<AssayTaskDto> dtos);
  List<AssayTask> fromDtoList(List<AssayTaskDto> dtos);
}
