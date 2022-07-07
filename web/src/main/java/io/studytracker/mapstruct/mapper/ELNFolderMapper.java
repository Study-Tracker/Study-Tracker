package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.response.ELNFolderDto;
import io.studytracker.model.ELNFolder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ELNFolderMapper {
  ELNFolderDto toDto(ELNFolder folder);

  ELNFolder fromDto(ELNFolderDto dto);
}
