/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
