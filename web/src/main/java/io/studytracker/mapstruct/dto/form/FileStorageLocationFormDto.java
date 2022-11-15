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

import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileStorageLocationFormDto {

  private Long id;
  private @NotNull Long integrationInstanceId;
  private StorageLocationType type;
  private @NotEmpty String displayName;
  private @NotEmpty String name;
  private String rootFolderPath;
  private String referenceId;
  private String url;
  private @NotNull StoragePermissions permissions;
  private boolean defaultStudyLocation = false;
  private boolean defaultDataLocation = false;
  private boolean active = true;

}
