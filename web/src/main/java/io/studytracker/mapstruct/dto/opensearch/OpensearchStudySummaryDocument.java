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

package io.studytracker.mapstruct.dto.opensearch;

import io.studytracker.model.Status;
import io.studytracker.search.StudySearchDocument;
import lombok.Data;

@Data
public class OpensearchStudySummaryDocument implements StudySearchDocument<Long> {

  private Long id;
  private String code;
  private String externalCode;
  private Status status;
  private String name;
  private String description;

}
