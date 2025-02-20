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

import io.studytracker.mapstruct.dto.api.AssayTypeDto;
import io.studytracker.mapstruct.dto.api.AssayTypePayloadDto;
import io.studytracker.mapstruct.dto.form.AssayTypeFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayTypeSlimDto;
import io.studytracker.model.AssayType;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeMapper {

  AssayTypeDetailsDto toDetailsDto(AssayType assayType);

  List<AssayTypeDetailsDto> toDetailsDtoList(List<AssayType> assayTypes);

  AssayTypeSlimDto toSlimDto(AssayType assayType);

  List<AssayTypeSlimDto> toSlimDtoList(List<AssayType> assayTypes);

  AssayType fromFormDto(AssayTypeFormDto dto);

  AssayType fromPayloadDto(AssayTypePayloadDto dto);

  AssayTypeDto toDto(AssayType assayType);
  List<AssayTypeDto> toDtoList(List<AssayType> assayTypes);
}
