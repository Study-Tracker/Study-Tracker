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

import io.studytracker.mapstruct.dto.api.AssayDto;
import io.studytracker.mapstruct.dto.api.AssayPayloadDto;
import io.studytracker.mapstruct.dto.form.AssayFormDto;
import io.studytracker.mapstruct.dto.response.AssayDetailsDto;
import io.studytracker.mapstruct.dto.response.AssayParentDto;
import io.studytracker.mapstruct.dto.response.AssaySlimDto;
import io.studytracker.mapstruct.dto.response.AssaySummaryDto;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayOptions;
import io.studytracker.model.AssayTask;
import io.studytracker.model.User;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AssayMapper {

  Assay fromAssayForm(AssayFormDto dto);

  Assay fromAssayDetails(AssayDetailsDto dto);

  Set<Assay> fromAssayDetailsSet(Set<AssayDetailsDto> dtos);

  List<Assay> fromAssayDetailsList(Set<AssayDetailsDto> dtos);

  AssayDetailsDto toAssayDetails(Assay assay);

  Set<AssayDetailsDto> toAssayDetailsSet(Set<Assay> assays);

  List<AssayDetailsDto> toAssayDetailsList(List<Assay> assays);

  Assay fromAssaySummary(AssaySummaryDto dto);

  Set<Assay> fromAssaySummarySet(Set<AssaySummaryDto> dtos);

  List<Assay> fromAssaySummaryList(Set<AssaySummaryDto> dtos);

  AssaySummaryDto toAssaySummary(Assay assay);

  Set<AssaySummaryDto> toAssaySummarySet(Set<Assay> assays);

  List<AssaySummaryDto> toAssaySummaryList(List<Assay> assays);

  Assay fromAssaySlim(AssaySlimDto dto);

  Set<Assay> fromAssaySlimSet(Set<AssaySlimDto> dtos);

  List<Assay> fromAssaySlimList(Set<AssaySlimDto> dtos);

  AssaySlimDto toAssaySlim(Assay assay);

  Set<AssaySlimDto> toAssaySlimSet(Set<Assay> assays);

  List<AssaySlimDto> toAssaySlimList(List<Assay> assays);

  Assay fromAssayParent(AssayParentDto dto);

  Set<Assay> fromAssayParentSet(Set<AssayParentDto> dtos);

  List<Assay> fromAssayParentList(Set<AssayParentDto> dtos);

  AssayParentDto toAssayParent(Assay assay);

  Set<AssayParentDto> toAssayParentSet(Set<Assay> assays);

  List<AssayParentDto> toAssayParentList(List<Assay> assays);

  @Mapping(target = "owner", source = "owner.id")
  @Mapping(target = "createdBy", source = "createdBy.id")
  @Mapping(target = "lastModifiedBy", source = "lastModifiedBy.id")
  @Mapping(target = "studyId", source = "study.id")
  @Mapping(target = "assayTypeId", source = "assayType.id")
  @Mapping(target = "notebookFolderId", source = "notebookFolder.id")
  @Mapping(target = "storageFolderId", source = "storageFolder.id")
  @Mapping(target = "users", source = "users", qualifiedByName = "userToId")
  @Mapping(target = "tasks", source = "tasks", qualifiedByName = "assayTaskToId")
  AssayDto toAssayDto(Assay assay);

  List<AssayDto> toAssayDtoList(List<Assay> assays);

  @Mapping(target = "owner", ignore = true)
  @Mapping(target = "users", ignore = true)
  Assay fromPayload(AssayPayloadDto dto);

  @Named("userToId")
  public static Long userToId(User user) { return user.getId();}

  @Named("assayTaskToId")
  public static Long assayTaskToId(AssayTask assayTask) { return assayTask.getId();}

  public AssayOptions optionsFromAssayPayload(AssayPayloadDto dto);

  public AssayOptions optionsFromAssayForm(AssayFormDto dto);

}
