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

import io.studytracker.mapstruct.dto.api.FileStoreFolderDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.model.FileStoreFolder;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileStoreFolderMapper {

  FileStoreFolder fromDetailsDto(FileStoreFolderDetailsDto dto);

  @Mapping(target = "fileStorageLocationId", source = "fileStorageLocation.id")
  FileStoreFolderDetailsDto toDetailsDto(FileStoreFolder folder);
  List<FileStoreFolderDetailsDto> toDetailsDtoList(List<FileStoreFolder> folders);
  Set<FileStoreFolderDetailsDto> toDetailsDtoSet(Set<FileStoreFolder> folders);

  @Mapping(target = "fileStorageLocationId", source = "fileStorageLocation.id")
  FileStoreFolderDto toDto(FileStoreFolder folder);
  List<FileStoreFolderDto> toDtoList(List<FileStoreFolder> folders);
  Set<FileStoreFolderDto> toDtoSet(Set<FileStoreFolder> folders);
}
