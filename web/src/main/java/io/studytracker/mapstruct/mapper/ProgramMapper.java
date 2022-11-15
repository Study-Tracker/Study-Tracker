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

import io.studytracker.mapstruct.dto.api.ProgramDto;
import io.studytracker.mapstruct.dto.api.ProgramPayloadDto;
import io.studytracker.mapstruct.dto.form.ProgramFormDto;
import io.studytracker.mapstruct.dto.response.ProgramDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.model.Program;
import io.studytracker.model.ProgramOptions;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgramMapper {

  // ProgramSummary
  Program fromProgramSummary(ProgramSummaryDto dto);

  List<Program> fromProgramSummaryList(List<ProgramSummaryDto> dtos);

  ProgramSummaryDto toProgramSummary(Program program);

  List<ProgramSummaryDto> toProgramSummaryList(List<Program> programs);

  // ProgramDetails
  Program fromProgramDetails(ProgramDetailsDto dto);

  List<Program> fromProgramDetailsList(List<ProgramDetailsDto> dtos);

  ProgramDetailsDto toProgramDetails(Program program);

  List<ProgramDetailsDto> toProgramDetailsList(List<Program> programs);

  ProgramFormDto toProgramFormDto(Program program);
  Program fromProgramFormDto(ProgramFormDto dto);

  // API
  List<ProgramDto> toProgramDtoList(List<Program> programs);
  @Mapping(source = "createdBy.id", target = "createdBy")
  @Mapping(source = "lastModifiedBy.id", target = "lastModifiedBy")
  @Mapping(source = "primaryStorageFolder.id", target = "storageFolderId")
  @Mapping(source = "notebookFolder.id", target = "notebookFolderId")
  ProgramDto toProgramDto(Program program);

  ProgramPayloadDto toProgramPayloadDto(Program program);
  Program fromProgramPayloadDto(ProgramPayloadDto dto);

  ProgramOptions optionsFromPayloadDto(ProgramPayloadDto dto);

  ProgramOptions optionsFromFormDto(ProgramFormDto dto);
}
