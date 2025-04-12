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
public class OpensearchStudyDocument implements StudySearchDocument<Long> {

  @Id private Long id;

  private String code;

  private String externalCode;

  private Status status;

  private String name;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchProgramDocument program;

  private String description;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchCollaboratorDocument collaborator;

  private boolean legacy;

  private boolean active;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchFolderDocument notebookFolder;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchFolderDocument storageFolder;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchUserDocument createdBy;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchUserDocument lastModifiedBy;

  private Date startDate;

  private Date endDate;

  private Date createdAt;

  private Date updatedAt;

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchUserDocument owner;

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<OpensearchUserDocument> users = new HashSet<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<OpensearchKeywordDocument> keywords = new HashSet<>();

  private Map<String, String> attributes = new HashMap<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<OpensearchLinkDocument> externalLinks = new HashSet<>();

  @Field(type = FieldType.Nested, includeInParent = true)
  private OpensearchConclusionsDocument conclusions;

  @Field(type = FieldType.Nested, includeInParent = true)
  private Set<OpensearchCommentDocument> comments = new HashSet<>();
}
