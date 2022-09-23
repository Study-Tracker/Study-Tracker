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

import io.studytracker.mapstruct.dto.api.StudyCollectionDto;
import io.studytracker.mapstruct.dto.api.StudyCollectionPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyCollectionFormDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyCollectionSummaryDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudyCollectionMapper {

  StudyCollection fromSummaryDto(StudyCollectionSummaryDto dto);

  List<StudyCollection> fromSummaryDtoList(List<StudyCollectionSummaryDto> dtos);

  StudyCollectionSummaryDto toSummaryDto(StudyCollection collection);

  List<StudyCollectionSummaryDto> toSummaryDtoList(List<StudyCollection> collections);

  StudyCollection fromDetailsDto(StudyCollectionDetailsDto dto);

  List<StudyCollection> fromDetailsDtoList(List<StudyCollectionDetailsDto> dtos);

  StudyCollectionDetailsDto toDetailsDto(StudyCollection collection);

  List<StudyCollectionDetailsDto> toDetailsDtoList(List<StudyCollection> collections);

  StudyCollection fromFormDto(StudyCollectionFormDto dto);

  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studies", source = "studies", qualifiedByName = "studyToId")
  StudyCollectionDto toDto(StudyCollection collection);
  List<StudyCollectionDto> toDtoList(List<StudyCollection> collections);

  @Mapping(target = "studies", ignore = true)
  StudyCollection fromPayloadDto(StudyCollectionPayloadDto dto);

  @Named("studyToId")
  public static Long studyToId(Study study) { return study.getId();}

}
