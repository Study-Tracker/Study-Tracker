package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.FileStoreFolderDto;
import io.studytracker.model.FileStoreFolder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileStoreFolderMapper {
  FileStoreFolder fromDto(FileStoreFolderDto dto);
  FileStoreFolderDto toDto(FileStoreFolder folder);
}
