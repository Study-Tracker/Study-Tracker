package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.NotebookEntryTemplateFormDto;
import io.studytracker.mapstruct.dto.response.NotebookEntryTemplateDetailsDto;
import io.studytracker.mapstruct.dto.response.NotebookEntryTemplateSlimDto;
import io.studytracker.model.NotebookEntryTemplate;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotebookEntryTemplateMapper {

  NotebookEntryTemplate fromForm(NotebookEntryTemplateFormDto dto);

  NotebookEntryTemplateDetailsDto toDetails(NotebookEntryTemplate template);

  List<NotebookEntryTemplateDetailsDto> toDetailsList(List<NotebookEntryTemplate> templates);

  NotebookEntryTemplate fromDetails(NotebookEntryTemplateDetailsDto dto);

  List<NotebookEntryTemplate> fromDetailsList(List<NotebookEntryTemplateDetailsDto> dtos);

  NotebookEntryTemplateSlimDto toSlim(NotebookEntryTemplate template);

  List<NotebookEntryTemplateSlimDto> toSlimList(List<NotebookEntryTemplate> templates);

  NotebookEntryTemplate fromSlim(NotebookEntryTemplateSlimDto dto);

  List<NotebookEntryTemplate> fromSlimList(List<NotebookEntryTemplateSlimDto> dtos);
}
