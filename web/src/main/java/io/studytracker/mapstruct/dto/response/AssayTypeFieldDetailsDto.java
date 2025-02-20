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

package io.studytracker.mapstruct.dto.response;

import io.studytracker.model.CustomEntityFieldType;
import lombok.Data;

@Data
public class AssayTypeFieldDetailsDto {

  private Long id;
  private String displayName;
  private String fieldName;
  private CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;
  private Integer fieldOrder;
  private String defaultValue;
  private String dropdownOptions;
}
