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

package io.studytracker.mapstruct.dto.api;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;

@Data
public class AssayTaskDto {

  private Long id;
  private Long assayId;
  private TaskStatus status;
  private String label;
  private Integer order;
  private Date createdAt;
  private Date updatedAt;
  private Long createdBy;
  private Long lastModifiedBy;
  private Long assignedTo;
  private Date dueDate;
}
