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

import io.studytracker.mapstruct.dto.api.ProgramDto;
import io.studytracker.mapstruct.dto.api.ProgramPayloadDto;
import io.studytracker.mapstruct.dto.form.ProgramFormDto;
import io.studytracker.mapstruct.dto.response.ProgramDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.model.Program;
import io.studytracker.model.ProgramNotebookFolder;
import io.studytracker.model.ProgramOptions;
import io.studytracker.model.ProgramStorageFolder;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {StorageDriveFolderMapper.class})
public interface ProgramMapper {

  // Summary
  ProgramSummaryDto toProgramSummary(Program program);

  List<ProgramSummaryDto> toProgramSummaryList(List<Program> programs);

  // ProgramDetails
  ProgramDetailsDto toProgramDetails(Program program);

  List<ProgramDetailsDto> toProgramDetailsList(List<Program> programs);

  // Form
  ProgramFormDto toProgramFormDto(Program program);

  @Mapping(source = "notebookFolder", target = "options.notebookFolder")
  @Mapping(source = "parentFolder", target = "options.parentFolder")
  @Mapping(source = "gitGroup", target = "options.gitGroup")
  @Mapping(source = "useGit", target = "options.useGit")
  @Mapping(source = "useNotebook", target = "options.useNotebook")
  Program fromProgramFormDto(ProgramFormDto dto);

  // API DTO
  List<ProgramDto> toProgramDtoList(List<Program> programs);
  @Mapping(source = "createdBy.id", target = "createdBy")
  @Mapping(source = "lastModifiedBy.id", target = "lastModifiedBy")
  @Mapping(source = "storageFolders", target = "storageFolders", qualifiedByName = "storageFolderToId")
  @Mapping(source = "notebookFolders", target = "notebookFolders", qualifiedByName = "notebookFolderToId")
  ProgramDto toProgramDto(Program program);

  // API Payload
  ProgramPayloadDto toProgramPayloadDto(Program program);

  @Mapping(source = "parentFolderId", target = "options.parentFolder.id")
  @Mapping(source = "notebookFolderId", target = "options.notebookFolder.id")
  Program fromProgramPayloadDto(ProgramPayloadDto dto);

  @Mapping(target = "parentFolder.id", source = "parentFolderId")
  ProgramOptions optionsFromPayloadDto(ProgramPayloadDto dto);

  ProgramOptions optionsFromFormDto(ProgramFormDto dto);

  @Named("storageFolderToId")
  public static Long storageFolderToId(ProgramStorageFolder folder) {
    return folder.getId();
  }

  @Named("notebookFolderToId")
  public static Long notebookFolderToId(ProgramNotebookFolder folder) {
    return folder.getId();
  }
}
