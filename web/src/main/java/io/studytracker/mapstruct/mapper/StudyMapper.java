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

import io.studytracker.mapstruct.dto.api.StudyDto;
import io.studytracker.mapstruct.dto.api.StudyPayloadDto;
import io.studytracker.mapstruct.dto.form.StudyFormDto;
import io.studytracker.mapstruct.dto.response.StudyDetailsDto;
import io.studytracker.mapstruct.dto.response.StudySlimDto;
import io.studytracker.mapstruct.dto.response.StudySummaryDto;
import io.studytracker.model.Comment;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Keyword;
import io.studytracker.model.Study;
import io.studytracker.model.StudyOptions;
import io.studytracker.model.StudyRelationship;
import io.studytracker.model.User;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StudyMapper {

  Study fromStudyForm(StudyFormDto dto);
  StudyFormDto toStudyForm(Study study);

  Study fromStudyDetails(StudyDetailsDto dto);

  List<Study> fromStudyDetailsList(List<StudyDetailsDto> dtos);

  Set<Study> fromStudyDetailsSet(Set<StudyDetailsDto> dtos);

  StudyDetailsDto toStudyDetails(Study study);

  List<StudyDetailsDto> toStudyDetailsList(List<Study> studies);

  Set<StudyDetailsDto> toStudyDetailsSet(Set<Study> studies);

  Study fromStudySummary(StudySummaryDto dto);

  List<Study> fromStudySummaryList(List<StudySummaryDto> dtos);

  Set<Study> fromStudySummarySet(Set<StudySummaryDto> dtos);

  StudySummaryDto toStudySummary(Study study);

  List<StudySummaryDto> toStudySummaryList(List<Study> studies);

  Set<StudySummaryDto> toStudySummarySet(Set<Study> studies);

  Study fromStudySlim(StudySlimDto dto);

  List<Study> fromStudySlimList(List<StudySlimDto> dtos);

  Set<Study> fromStudySlimSet(Set<StudySlimDto> dtos);

  StudySlimDto toStudySlim(Study study);

  List<StudySlimDto> toStudySlimList(List<Study> studies);

  Set<StudySlimDto> toStudySlimSet(Set<Study> studies);

  @Mapping(target = "owner", source = "owner.id")
  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "programId", source = "program.id")
  @Mapping(target = "collaboratorId", source = "collaborator.id")
  @Mapping(target = "notebookFolderId", source = "notebookFolder.id")
  @Mapping(target = "users", source = "users", qualifiedByName = "userToId")
  @Mapping(target = "comments", source = "comments", qualifiedByName = "commentToId")
  @Mapping(target = "keywords", source = "keywords", qualifiedByName = "keywordToId")
  @Mapping(target = "externalLinks", source = "externalLinks", qualifiedByName = "externalLinkToId")
  @Mapping(target = "studyRelationships", source = "studyRelationships", qualifiedByName = "studyRelationshipToId")
  StudyDto toDto(Study study);

  List<StudyDto> toDtoList(List<Study> studies);

  @Mapping(target = "owner", ignore = true)
  @Mapping(target = "users", ignore = true)
  @Mapping(target = "keywords", ignore = true)
  Study fromPayload(StudyPayloadDto dto);

  @Named("userToId")
  public static Long userToId(User user) { return user.getId();}

  @Named("keywordToId")
  public static Long keywordToId(Keyword keyword) { return keyword.getId();}

  @Named("externalLinkToId")
  public static Long externalLinkToId(ExternalLink link) { return link.getId();}

  @Named("commentToId")
  public static Long commentToId(Comment comment) { return comment.getId();}

  @Named("studyRelationshipToId")
  public static Long studyRelationshipToId(StudyRelationship relationship) { return relationship.getId();}

  StudyOptions optionsFromStudyPayload(StudyPayloadDto dto);
  StudyOptions optionsFromStudyForm(StudyFormDto dto);

}
