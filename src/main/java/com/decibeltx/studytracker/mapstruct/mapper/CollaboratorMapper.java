package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.CollaboratorDto;
import com.decibeltx.studytracker.model.Collaborator;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {
  Collaborator fromDto(CollaboratorDto dto);
  List<Collaborator> fromDtoList(List<CollaboratorDto> dtos);
  CollaboratorDto toDto(Collaborator collaborator);
  List<CollaboratorDto> toDtoList(List<Collaborator> collaborators);
}
