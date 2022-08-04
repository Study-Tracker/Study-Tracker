package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.NotebookFolderDto;
import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.model.ELNFolder;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotebookFolderMapper {
  NotebookFolderDetailsDto toDetailsDto(ELNFolder folder);

  ELNFolder fromDto(NotebookFolderDetailsDto dto);

  NotebookFolderDto toDto(ELNFolder folder);
  List<NotebookFolderDto> toDtoList(List<ELNFolder> folders);
}
