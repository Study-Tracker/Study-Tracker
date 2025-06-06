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

package io.studytracker.model;

import io.studytracker.mapstruct.dto.response.NotebookFolderDetailsDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AssayOptions {

  private boolean useNotebook = true;
  private NotebookFolderDetailsDto notebookFolder;
  private String notebookTemplateId;
  private boolean useGit = false;
  private boolean useStorage = true;
  private boolean useS3 = false;
  private Long s3FolderId;
  private StorageDriveFolder parentFolder;
  private List<StorageDriveFolder> additionalFolders = new ArrayList<>();
  private GitGroup gitGroup;

}
