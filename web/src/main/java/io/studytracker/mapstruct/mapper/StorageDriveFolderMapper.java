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

import io.studytracker.mapstruct.dto.api.AssayStorageDriveFolderDto;
import io.studytracker.mapstruct.dto.api.ProgramStorageDriveFolderDto;
import io.studytracker.mapstruct.dto.api.StudyStorageDriveFolderDto;
import io.studytracker.mapstruct.dto.response.AssayStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.dto.response.ProgramStorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.dto.response.StorageDriveFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.StorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.dto.response.StudyStorageDriveFolderSummaryDto;
import io.studytracker.model.AssayStorageFolder;
import io.studytracker.model.ProgramStorageFolder;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.StudyStorageFolder;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StorageDriveFolderMapper {

  StorageDriveFolderDetailsDto toDetailsDto(StorageDriveFolder folder);
  List<StorageDriveFolderDetailsDto> toDetailsDto(List<StorageDriveFolder> folders);
  Set<StorageDriveFolderDetailsDto> toDetailsDto(Set<StorageDriveFolder> folders);

  @Mapping(target = "storageDriveId", source = "storageDrive.id")
  StorageDriveFolderSummaryDto toSummaryDto(StorageDriveFolder folder);
  List<StorageDriveFolderSummaryDto> toSummaryDto(List<StorageDriveFolder> folders);
  Set<StorageDriveFolderSummaryDto> toSummaryDto(Set<StorageDriveFolder> folders);

  // Program folder

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "programId", source = "program.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  ProgramStorageDriveFolderSummaryDto toProgramFolderSummaryDto(ProgramStorageFolder folder);
  List<ProgramStorageDriveFolderSummaryDto> toProgramFolderSummaryDto(List<ProgramStorageFolder> folders);
  Set<ProgramStorageDriveFolderSummaryDto> toProgramFolderSummaryDto(Set<ProgramStorageFolder> folders);

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "programId", source = "program.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  ProgramStorageDriveFolderDto toProgramFolderDto(ProgramStorageFolder folder);
  List<ProgramStorageDriveFolderDto> toProgramFolderDto(List<ProgramStorageFolder> folders);
  Set<ProgramStorageDriveFolderDto> toProgramFolderDto(Set<ProgramStorageFolder> folders);

  // Study folder

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "studyId", source = "study.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  StudyStorageDriveFolderSummaryDto toStudyFolderSummaryDto(StudyStorageFolder folder);
  List<StudyStorageDriveFolderSummaryDto> toStudyFolderSummaryDto(List<StudyStorageFolder> folders);
  Set<StudyStorageDriveFolderSummaryDto> toStudyFolderSummaryDto(Set<StudyStorageFolder> folders);

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "studyId", source = "study.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  StudyStorageDriveFolderDto toStudyFolderDto(StudyStorageFolder folder);
  List<StudyStorageDriveFolderDto> toStudyFolderDto(List<StudyStorageFolder> folders);
  Set<StudyStorageDriveFolderDto> toStudyFolderDto(Set<StudyStorageFolder> folders);

  // Assay folder

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "assayId", source = "assay.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  AssayStorageDriveFolderSummaryDto toAssayFolderSummaryDto(AssayStorageFolder folder);
  List<AssayStorageDriveFolderSummaryDto> toAssayFolderSummaryDto(List<AssayStorageFolder> folders);
  Set<AssayStorageDriveFolderSummaryDto> toAssayFolderSummaryDto(Set<AssayStorageFolder> folders);

  @Mapping(target = "storageDriveId", source = "storageDriveFolder.storageDrive.id")
  @Mapping(target = "storageDriveFolderId", source = "storageDriveFolder.id")
  @Mapping(target = "assayId", source = "assay.id")
  @Mapping(target = "name", source = "storageDriveFolder.name")
  @Mapping(target = "path", source = "storageDriveFolder.path")
  @Mapping(target = "writeEnabled", source = "storageDriveFolder.writeEnabled")
  @Mapping(target = "deleteEnabled", source = "storageDriveFolder.name")
  AssayStorageDriveFolderDto toAssayFolderDto(AssayStorageFolder folder);
  List<AssayStorageDriveFolderDto> toAssayFolderDto(List<AssayStorageFolder> folders);
  Set<AssayStorageDriveFolderDto> toAssayFolderDto(Set<AssayStorageFolder> folders);

  @Named("storageDriveFolderToId")
  public static Long storageDriveFolderToId(StorageDriveFolder folder) {
    return folder == null ? null : folder.getId();
  }

  @Named("programStorageFolderToId")
  public static Long programStorageFolderToId(ProgramStorageFolder folder) {
    return folder == null ? null : folder.getId();
  }

  @Named("studyStorageFolderToId")
  public static Long studyStorageFolderToId(StudyStorageFolder folder) {
    return folder == null ? null : folder.getId();
  }

  @Named("assayStorageFolderToId")
  public static Long assayStorageFolderToId(AssayStorageFolder folder) {
    return folder == null ? null : folder.getId();
  }

}
