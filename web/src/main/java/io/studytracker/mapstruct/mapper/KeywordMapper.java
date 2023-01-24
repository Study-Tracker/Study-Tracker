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

import io.studytracker.mapstruct.dto.api.KeywordDto;
import io.studytracker.mapstruct.dto.api.KeywordPayloadDto;
import io.studytracker.mapstruct.dto.form.KeywordFormDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface KeywordMapper {
  KeywordDetailsDto toDetailsDto(Keyword keyword);

  List<KeywordDetailsDto> toDetailsDtoList(List<Keyword> keywords);

  Set<KeywordDetailsDto> toDetailsDtoSet(Set<Keyword> keywords);

  Keyword fromDetailsDto(KeywordDetailsDto dto);

  List<Keyword> fromDetailsDtoList(List<KeywordDetailsDto> dtos);

  Set<Keyword> fromDetailsDtoSet(Set<KeywordDetailsDto> dtos);

  Keyword fromFormDto(KeywordFormDto dto);

  List<Keyword> fromFormDtoList(List<KeywordFormDto> dtos);

  Set<Keyword> fromFormDtoSet(Set<KeywordFormDto> dtos);

  @Mapping(target = "categoryId", source = "category.id")
  KeywordDto toDto(Keyword keyword);
  List<KeywordDto> toDtoList(List<Keyword> keywords);
  Set<KeywordDto> toDtoSet(Set<Keyword> keywords);
  Keyword fromPayloadDto(KeywordPayloadDto dto);
}
