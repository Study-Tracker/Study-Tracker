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

package io.studytracker.egnyte;

import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.service.NamingService;

public class EgnyteFolderNamingService extends NamingService {

  @Override
  public String getStudyStorageFolderName(Study study) {
    return super.getStudyStorageFolderName(study)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  @Override
  public String getAssayStorageFolderName(Assay assay) {
    return super.getAssayStorageFolderName(assay)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  @Override
  public String getProgramStorageFolderName(Program program) {
    return super.getProgramStorageFolderName(program)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }
}
