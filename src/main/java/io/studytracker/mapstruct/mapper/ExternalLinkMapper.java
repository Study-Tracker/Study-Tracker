package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.ExternalLinkDto;
import io.studytracker.model.ExternalLink;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalLinkMapper {
  ExternalLinkDto toDto(ExternalLink link);

  List<ExternalLinkDto> toDtoList(List<ExternalLink> links);

  ExternalLink fromDto(ExternalLinkDto dto);

  List<ExternalLink> fromDtoList(List<ExternalLinkDto> dtos);
}
