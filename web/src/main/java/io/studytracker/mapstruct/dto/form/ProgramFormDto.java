/*
 * Copyright 2019-2023 the original author or authors.
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

import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import io.studytracker.mapstruct.dto.response.StorageDriveFolderSummaryDto;
import io.studytracker.mapstruct.dto.response.UserSummaryDto;
import io.studytracker.model.GitGroup;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgramFormDto {

  private Long id;
  private @NotNull(message = "Program code must not be empty") String code;
  private @NotNull(message = "Program name must not be empty") String name;
  private String description;
  private UserSummaryDto createdBy;
  private UserSummaryDto lastModifiedBy;
  private Date createdAt;
  private Date updatedAt;
  private boolean active;
  private NotebookFolderDetailsDto notebookFolder;
  private StorageDriveFolderSummaryDto parentFolder;
  private Map<String, String> attributes = new HashMap<>();
  private boolean useGit;
  private GitGroup gitGroup;
  private boolean useNotebook;
}
