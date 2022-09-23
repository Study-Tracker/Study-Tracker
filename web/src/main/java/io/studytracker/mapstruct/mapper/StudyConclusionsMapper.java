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

import io.studytracker.mapstruct.dto.api.StudyConclusionsDto;
import io.studytracker.mapstruct.dto.api.StudyConclusionsPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyConclusionsFormDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDetailsDto;
import io.studytracker.model.StudyConclusions;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyConclusionsMapper {
  StudyConclusions fromDetailsDto(StudyConclusionsDetailsDto dto);
  StudyConclusions fromFormDto(StudyConclusionsFormDto dto);

  StudyConclusionsDetailsDto toDetailsDto(StudyConclusions conclusions);

  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studyId", source = "study.id")
  StudyConclusionsDto toDto(StudyConclusions conclusions);

  List<StudyConclusionsDto> toDtoList(List<StudyConclusions> conclusions);
  StudyConclusions fromPayload(StudyConclusionsPayloadDto dto);
}
