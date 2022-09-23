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

package io.studytracker.mapstruct.dto.form;

import io.studytracker.mapstruct.dto.response.CollaboratorDetailsDto;
import io.studytracker.mapstruct.dto.response.CommentDetailsDto;
import io.studytracker.mapstruct.dto.response.ExternalLinkDetailsDto;
import io.studytracker.mapstruct.dto.response.FileStoreFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.KeywordDetailsDto;
import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.ProgramSummaryDto;
import io.studytracker.mapstruct.dto.response.StudyConclusionsDetailsDto;
import io.studytracker.mapstruct.dto.response.StudyRelationshipDetailsDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import io.studytracker.model.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudyFormDto {

  private Long id;
  private String code;
  private String externalCode;
  private @NotNull Status status;
  private @NotBlank String name;
  private ProgramSummaryDto program;
  private @NotBlank String description;
  private String notebookTemplateId;
  private CollaboratorDetailsDto collaborator;
  private boolean legacy = false;
  private boolean active = true;
  private boolean external = false;
  private NotebookFolderDetailsDto notebookFolder;
  private FileStoreFolderDetailsDto storageFolder;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private @NotNull Date startDate;
  private Date endDate;
  private Date createdAt;
  private Date updatedAt;
  private UserSummaryDto owner;
  private Set<UserSummaryDto> users = new HashSet<>();
  private Set<KeywordDetailsDto> keywords = new HashSet<>();
  private Map<String, String> attributes = new HashMap<>();
  private Set<ExternalLinkDetailsDto> externalLinks = new HashSet<>();
  private Set<StudyRelationshipDetailsDto> studyRelationships = new HashSet<>();
  private StudyConclusionsDetailsDto conclusions;
  private Set<CommentDetailsDto> comments = new HashSet<>();
}
