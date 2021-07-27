package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.ELNFolderDto;
import com.decibeltx.studytracker.model.ELNFolder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ELNFolderMapper {
  ELNFolderDto toDto(ELNFolder folder);
  ELNFolder fromDto(ELNFolderDto dto);
}
