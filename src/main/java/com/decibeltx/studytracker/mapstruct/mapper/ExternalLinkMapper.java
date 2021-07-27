package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.ExternalLinkDto;
import com.decibeltx.studytracker.model.ExternalLink;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExternalLinkMapper {
  ExternalLinkDto toDto(ExternalLink link);
  List<ExternalLinkDto> toDtoList(List<ExternalLink> links);
  ExternalLink fromDto(ExternalLinkDto dto);
  List<ExternalLink> fromDtoList(List<ExternalLinkDto> dtos);
}
