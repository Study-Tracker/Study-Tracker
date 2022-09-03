package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.api.ExternalLinkDto;
import io.studytracker.mapstruct.dto.api.ExternalLinkPayloadDto;
import io.studytracker.mapstruct.dto.form.ExternalLinkFormDto;
import io.studytracker.mapstruct.dto.response.ExternalLinkDetailsDto;
import io.studytracker.model.ExternalLink;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExternalLinkMapper {
  ExternalLinkDetailsDto toDetailsDto(ExternalLink link);

  List<ExternalLinkDetailsDto> toDetailsDtoList(List<ExternalLink> links);

  ExternalLink fromDetailsDto(ExternalLinkDetailsDto dto);

  List<ExternalLink> fromDetailsDtoList(List<ExternalLinkDetailsDto> dtos);

  ExternalLink fromFormDto(ExternalLinkFormDto dto);

  @Mapping(target = "studyId", source = "study.id")
  ExternalLinkDto toDto(ExternalLink link);
  List<ExternalLinkDto> toDtoList(List<ExternalLink> links);

  ExternalLink fromPayloadDto(ExternalLinkPayloadDto dto);
}
