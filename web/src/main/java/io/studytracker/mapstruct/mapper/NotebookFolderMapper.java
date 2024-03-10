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

import io.studytracker.mapstruct.dto.api.NotebookFolderDto;
import io.studytracker.mapstruct.dto.response.AssayNotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramNotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyNotebookFolderDetailsDto;
import io.studytracker.model.AssayNotebookFolder;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.ProgramNotebookFolder;
import io.studytracker.model.StudyNotebookFolder;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotebookFolderMapper {
  NotebookFolderDetailsDto toDetailsDto(ELNFolder folder);

  ELNFolder fromDto(NotebookFolderDetailsDto dto);

  NotebookFolderDto toDto(ELNFolder folder);
  List<NotebookFolderDto> toDtoList(List<ELNFolder> folders);

  // Program
  ProgramNotebookFolderDetailsDto toProgramFolderDto(ProgramNotebookFolder folder);
  Set<ProgramNotebookFolderDetailsDto> toProgramFolderDto(Set<ProgramNotebookFolder> folders);
  List<ProgramNotebookFolderDetailsDto> toProgramFolderDto(List<ProgramNotebookFolder> folders);

  // Study
  StudyNotebookFolderDetailsDto toStudyFolderDto(StudyNotebookFolder folder);
  Set<StudyNotebookFolderDetailsDto> toStudyFolderDto(Set<StudyNotebookFolder> folders);
  List<StudyNotebookFolderDetailsDto> toStudyFolderDto(List<StudyNotebookFolder> folders);

  // Assay
  AssayNotebookFolderDetailsDto toAssayFolderDto(AssayNotebookFolder folder);
  Set<AssayNotebookFolderDetailsDto> toAssayFolderDto(Set<AssayNotebookFolder> folders);
  List<AssayNotebookFolderDetailsDto> toAssayFolderDto(List<AssayNotebookFolder> folders);

}
