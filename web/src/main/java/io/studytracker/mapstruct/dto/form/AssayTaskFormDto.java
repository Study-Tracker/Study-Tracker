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

import io.studytracker.mapstruct.dto.response.UserSlimDto;
import io.studytracker.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class AssayTaskFormDto {

  private Long id;
  @NotNull private TaskStatus status;
  @NotNull private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
  private UserSlimDto createdBy;
  private UserSlimDto lastModifiedBy;
  private UserSlimDto assignedTo;
  private Date dueDate;
  private Set<AssayTaskFieldFormDto> fields = new HashSet<>();
  private Map<String, Object> data = new HashMap<>();

}
