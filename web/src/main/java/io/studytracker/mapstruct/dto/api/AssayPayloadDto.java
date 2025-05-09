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

package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayPayloadDto {

  private Long id;
  private Status status;
  @NotNull private Long assayTypeId;
  @NotNull private Long studyId;
  @NotBlank private String name;
  private String code;
  @NotBlank private String description;
  private Long owner;
  @NotNull private Date startDate;
  private Date endDate;
  private NotebookFolderPayloadDto notebookFolder;
  private FileStoreFolderPayloadDto storageFolder;
  private boolean active = true;
  private String notebookTemplateId;
  private Set<Long> users = new HashSet<>();
  private Map<String, Object> fields = new LinkedHashMap<>();
  private Map<String, String> attributes = new HashMap<>();
  private boolean useNotebook = true;
  private boolean useGit = false;
  private boolean useStorage = true;
}
