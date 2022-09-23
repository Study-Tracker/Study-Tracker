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

import io.studytracker.model.Status;
import io.studytracker.search.AssaySearchDocument;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "st-assays")
@Data
public class ElasticsearchAssayDocument implements AssaySearchDocument<Long> {

  @Id private Long id;

  private String code;

  private String name;

  private String description;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchAssayTypeDocument assayType;

  private Status status;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchStudySummaryDocument study;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument createdBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument lastModifiedBy;

  private Date startDate;

  private Date endDate;

  private Date createdAt;

  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchUserDocument owner;

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchUserDocument> users = new HashSet<>();

  private Map<String, String> attributes = new HashMap<>();

  private boolean active;

  private Map<String, Object> fields = new HashMap<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchAssayTaskDocument> tasks;

}
