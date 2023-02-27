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

import io.studytracker.mapstruct.dto.response.StorageDriveFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.StorageDriveFolderSummaryDto;
import io.studytracker.model.StorageDriveFolder;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StorageDriveFolderMapper {

  StorageDriveFolderDetailsDto toDetailsDto(StorageDriveFolder folder);
  List<StorageDriveFolderDetailsDto> toDetailsDto(List<StorageDriveFolder> folders);
  Set<StorageDriveFolderDetailsDto> toDetailsDto(Set<StorageDriveFolder> folders);

  @Mapping(target = "storageDriveId", source = "storageDrive.id")
  StorageDriveFolderSummaryDto toSummaryDto(StorageDriveFolder folder);
  List<StorageDriveFolderSummaryDto> toSummaryDto(List<StorageDriveFolder> folders);
  Set<StorageDriveFolderSummaryDto> toSummaryDto(Set<StorageDriveFolder> folders);

}
