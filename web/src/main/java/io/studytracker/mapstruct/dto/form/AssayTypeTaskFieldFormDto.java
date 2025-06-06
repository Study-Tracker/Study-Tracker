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

import io.studytracker.model.CustomEntityFieldType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssayTypeTaskFieldFormDto {

  private Long id;
  private @NotEmpty String displayName;
  private String fieldName;
  private @NotNull CustomEntityFieldType type;
  private boolean required = false;
  private String description;
  private boolean active = true;
  private @NotNull Integer fieldOrder;
  private String defaultValue;
  private String dropdownOptions;
}
