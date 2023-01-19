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

import io.studytracker.mapstruct.dto.api.AssayTypeFieldDto;
import io.studytracker.mapstruct.dto.api.AssayTypeFieldPayloadDto;
import io.studytracker.mapstruct.dto.form.AssayTypeFieldFormDto;
import io.studytracker.mapstruct.dto.response.AssayTypeFieldDetailsDto;
import io.studytracker.model.AssayTypeField;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssayTypeFieldMapper {

  AssayTypeFieldDetailsDto toDetailsDto(AssayTypeField field);
  Set<AssayTypeFieldDetailsDto> toDetailsDtoSet(Set<AssayTypeField> fields);
  List<AssayTypeFieldDetailsDto> toDetailsDtoList(List<AssayTypeField> fields);

  AssayTypeField fromFormDto(AssayTypeFieldFormDto dto);
  List<AssayTypeField> fromFormDtoList(List<AssayTypeFieldFormDto> dtoList);
  Set<AssayTypeField> fromFormDtoSet(Set<AssayTypeFieldFormDto> dtoSet);


  AssayTypeFieldDto toDto(AssayTypeField field);
  List<AssayTypeFieldDto> toDtoList(List<AssayTypeField> fields);
  Set<AssayTypeFieldDto> toDtoSet(Set<AssayTypeField> fields);

  AssayTypeField fromPayloadDto(AssayTypeFieldPayloadDto dto);
  List<AssayTypeField> fromPayloadDtoList(List<AssayTypeFieldPayloadDto> dtoList);
  Set<AssayTypeField> fromPayloadDtoSet(Set<AssayTypeFieldPayloadDto> dtoSet);

}
