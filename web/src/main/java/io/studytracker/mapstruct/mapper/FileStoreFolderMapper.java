package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.FileStoreFolderDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.model.FileStoreFolder;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileStoreFolderMapper {
  FileStoreFolder fromDetailsDto(FileStoreFolderDetailsDto dto);

  FileStoreFolderDetailsDto toDetailsDto(FileStoreFolder folder);

  FileStoreFolderDto toDto(FileStoreFolder folder);
  List<FileStoreFolderDto> toDto(List<FileStoreFolder> folders);
}
