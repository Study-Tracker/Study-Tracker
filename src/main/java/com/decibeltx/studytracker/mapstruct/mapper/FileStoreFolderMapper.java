package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.FileStoreFolderDto;
import com.decibeltx.studytracker.model.FileStoreFolder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileStoreFolderMapper {
  FileStoreFolder fromDto(FileStoreFolderDto dto);
  FileStoreFolderDto toDto(FileStoreFolder folder);
}
