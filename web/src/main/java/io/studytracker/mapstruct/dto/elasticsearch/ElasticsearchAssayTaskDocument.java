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

package io.studytracker.mapstruct.dto.elasticsearch;

import io.studytracker.model.TaskStatus;
import java.util.Date;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
public class ElasticsearchAssayTaskDocument {

  private Long id;

  private TaskStatus status;

  private String label;

  private Integer order;

  private Date createdAt;

  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument createdBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument lastModifiedBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument assignedTo;

  private Date dueDate;

}
