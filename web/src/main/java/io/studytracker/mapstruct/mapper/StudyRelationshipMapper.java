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

import io.studytracker.mapstruct.dto.api.StudyRelationshipDto;
import io.studytracker.mapstruct.dto.api.StudyRelationshipPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyRelationshipFormDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipSlimDto;
import io.studytracker.model.StudyRelationship;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyRelationshipMapper {

  StudyRelationship fromDetails(StudyRelationshipDetailsDto dto);

  StudyRelationship fromFormDto(StudyRelationshipFormDto dto);

  List<StudyRelationship> fromDetailsList(List<StudyRelationshipDetailsDto> dtos);

  Set<StudyRelationship> fromDetailsSet(Set<StudyRelationshipDetailsDto> dtos);

  StudyRelationshipDetailsDto toDetails(StudyRelationship relationship);

  List<StudyRelationshipDetailsDto> toDetailsList(List<StudyRelationship> relationships);

  Set<StudyRelationship> toDetailsSet(Set<StudyRelationship> relationships);

  @Mapping(source = "sourceStudy.id", target = "sourceStudyId")
  @Mapping(source = "targetStudy.id", target = "targetStudyId")
  StudyRelationshipSlimDto toSlim(StudyRelationship relationship);

  List<StudyRelationshipSlimDto> toSlimList(List<StudyRelationship> relationships);

  Set<StudyRelationshipSlimDto> toSlimSet(Set<StudyRelationship> relationships);

  @Mapping(source = "sourceStudy.id", target = "sourceStudyId")
  @Mapping(source = "targetStudy.id", target = "targetStudyId")
  StudyRelationshipDto toDto(StudyRelationship relationship);
  List<StudyRelationshipDto> toDtoList(List<StudyRelationship> relationships);
  StudyRelationship fromPayload(StudyRelationshipPayloadDto dto);
}
