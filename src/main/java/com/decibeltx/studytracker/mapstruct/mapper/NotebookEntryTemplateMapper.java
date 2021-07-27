package com.decibeltx.studytracker.mapstruct.mapper;

import com.decibeltx.studytracker.mapstruct.dto.NotebookEntryTemplateDetailsDto;
import com.decibeltx.studytracker.mapstruct.dto.NotebookEntryTemplateSlimDto;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotebookEntryTemplateMapper {

  NotebookEntryTemplateDetailsDto toDetails(NotebookEntryTemplate template);
  List<NotebookEntryTemplateDetailsDto> toDetailsList(List<NotebookEntryTemplate> templates);
  NotebookEntryTemplate fromDetails(NotebookEntryTemplateDetailsDto dto);
  List<NotebookEntryTemplate> fromDetailsList(List<NotebookEntryTemplateDetailsDto> dtos);

  NotebookEntryTemplateSlimDto toSlim(NotebookEntryTemplate template);
  List<NotebookEntryTemplateSlimDto> toSlimList(List<NotebookEntryTemplate> templates);
  NotebookEntryTemplate fromSlim(NotebookEntryTemplateSlimDto dto);
  List<NotebookEntryTemplate> fromSlimList(List<NotebookEntryTemplateSlimDto> dtos);

}
