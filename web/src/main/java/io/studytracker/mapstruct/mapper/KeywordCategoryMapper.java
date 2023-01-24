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

import io.studytracker.mapstruct.dto.api.KeywordCategoryDto;
import io.studytracker.mapstruct.dto.api.KeywordCategoryPayloadDto;
import io.studytracker.mapstruct.dto.form.KeywordCategoryFormDto;
import io.studytracker.mapstruct.dto.response.KeywordCategoryDetailsDto;
import io.studytracker.model.KeywordCategory;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface KeywordCategoryMapper {

  KeywordCategory fromKeywordCategoryDto(KeywordCategoryDetailsDto dto);
  KeywordCategory fromKeywordCategoryFormDto(KeywordCategoryFormDto dto);
  Set<KeywordCategory> fromKeywordCategoryDtoSet(Set<KeywordCategoryDetailsDto> dtos);
  List<KeywordCategory> fromKeywordCategoryDtoList(List<KeywordCategoryDetailsDto> dtos);

  KeywordCategoryDetailsDto toKeywordCategoryDto(KeywordCategory category);
  Set<KeywordCategoryDetailsDto> toKeywordCategoryDtoSet(Set<KeywordCategory> categories);
  List<KeywordCategoryDetailsDto> toKeywordCategoryDtoList(List<KeywordCategory> categories);

  KeywordCategoryDto toDto(KeywordCategory category);
  List<KeywordCategoryDto> toDtoList(List<KeywordCategory> categories);
  KeywordCategory fromPayloadDto(KeywordCategoryPayloadDto dto);

}
