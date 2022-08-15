package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.CollaboratorDto;
import io.studytracker.mapstruct.dto.api.CollaboratorPayloadDto;
import io.studytracker.mapstruct.dto.form.CollaboratorFormDto;
import io.studytracker.mapstruct.dto.response.CollaboratorDetailsDto;
import io.studytracker.model.Collaborator;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollaboratorMapper {
  Collaborator fromDetailsDto(CollaboratorDetailsDto dto);

  List<Collaborator> fromDetailsDtoList(List<CollaboratorDetailsDto> dtos);

  CollaboratorDetailsDto toDetailsDto(Collaborator collaborator);

  List<CollaboratorDetailsDto> toDetailsDtoList(List<Collaborator> collaborators);
  Collaborator fromFormDto(CollaboratorFormDto dto);
  Collaborator fromPayloadDto(CollaboratorPayloadDto dto);
  CollaboratorDto toDto(Collaborator collaborator);
  List<CollaboratorDto> toDtoList(List<Collaborator> collaborators);
}
