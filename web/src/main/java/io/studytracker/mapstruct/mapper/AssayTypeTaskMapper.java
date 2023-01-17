/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.mapstruct.dto.api.AssayTypeTaskDto;
import io.studytracker.mapstruct.dto.api.AssayTypeTaskPayloadDto;
import io.studytracker.mapstruct.dto.form.AssayTypeTaskFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeTaskDetailsDto;
import io.studytracker.model.AssayTypeTask;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeTaskMapper {

  AssayTypeTaskDetailsDto toDetailsDto(AssayTypeTask task);

  List<AssayTypeTaskDetailsDto> toDetailsDtoList(List<AssayTypeTask> tasks);
  Set<AssayTypeTaskDetailsDto> toDetailsDtoSet(Set<AssayTypeTask> tasks);

  AssayTypeTaskDto toDto(AssayTypeTask task);
  List<AssayTypeTaskDto> toDtoList(List<AssayTypeTask> tasks);
  Set<AssayTypeTaskDto> toDtoSet(Set<AssayTypeTask> tasks);
  AssayTypeTask fromPayloadDto(AssayTypeTaskPayloadDto dto);
  Set<AssayTypeTask> fromPayloadDtoSet(Set<AssayTypeTaskPayloadDto> dtos);
  List<AssayTypeTask> fromPayloadDtoList(List<AssayTypeTaskPayloadDto> dtos);
  AssayTypeTask fromFormDto(AssayTypeTaskFormDto dto);
  Set<AssayTypeTask> fromFormDtoSet(Set<AssayTypeTaskFormDto> dto);
  List<AssayTypeTask> fromFormDtoList(List<AssayTypeTaskFormDto> dto);

}
