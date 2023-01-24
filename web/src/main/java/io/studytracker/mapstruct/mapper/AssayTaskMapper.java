/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

  AssayTask fromFormDto(AssayTaskFormDto dto);
  List<AssayTask> fromFormDtoList(List<AssayTaskFormDto> dtoList);
  Set<AssayTask> fromFormDtoSet(Set<AssayTaskFormDto> dtoSet);

  @Mapping(target = "assignedTo", source = "assignedTo.id")
  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "assayId", source = "assay.id")
  AssayTaskDto toDto(AssayTask task);
  List<AssayTaskDto> toDtoList(List<AssayTask> tasks);
  Set<AssayTask> toDtoSet(Set<AssayTask> tasks);

  @Mapping(target = "assignedTo", ignore = true)
  AssayTask fromPayload(AssayTaskPayloadDto dto);
  List<AssayTask> fromPayloadList(List<AssayTaskPayloadDto> dtoList);
  Set<AssayTask> fromPayloadSet(Set<AssayTaskPayloadDto> dtoSet);
}
