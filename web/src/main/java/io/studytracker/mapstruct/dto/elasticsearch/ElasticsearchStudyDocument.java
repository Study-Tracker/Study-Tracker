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
import io.studytracker.search.StudySearchDocument;
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

@Document(indexName = "st-studies")
@Data
public class ElasticsearchStudyDocument implements StudySearchDocument<Long> {

  @Id private Long id;

  private String code;

  private String externalCode;

  private Status status;

  private String name;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchProgramDocument program;

  private String description;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchCollaboratorDocument collaborator;

  private boolean legacy;

  private boolean active;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchFolderDocument notebookFolder;

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchFolderDocument storageFolder;

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

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchKeywordDocument> keywords = new HashSet<>();

  private Map<String, String> attributes = new HashMap<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchLinkDocument> externalLinks = new HashSet<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private ElasticsearchConclusionsDocument conclusions;

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<ElasticsearchCommentDocument> comments = new HashSet<>();
}
