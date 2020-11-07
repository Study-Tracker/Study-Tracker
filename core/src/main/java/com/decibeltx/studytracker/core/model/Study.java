/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.core.model;

import com.decibeltx.studytracker.core.eln.NotebookFolder;
import com.decibeltx.studytracker.core.keyword.Keyword;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "studies")
@Data
public class Study implements Persistable<String> {

  @Id
  private String id;

  @Indexed(unique = true)
  @NotNull
  private String code;

  private String externalCode;

  @NotNull
  private Status status;

  @NotNull
  private String name;

  @Linked(model = Program.class)
  @NotNull
  @DBRef
  private Program program;

  @NotNull
  private String description;

  @Linked(model = Collaborator.class)
  @DBRef
  private Collaborator collaborator;

  private boolean legacy = false;

  private boolean active = true;

  private NotebookFolder notebookFolder;

  private StorageFolder storageFolder;

  @CreatedBy
  @Linked(model = User.class)
  @NotNull
  @DBRef
  private User createdBy;

  @LastModifiedBy
  @Linked(model = User.class)
  @NotNull
  @DBRef
  private User lastModifiedBy;

  @NotNull
  private Date startDate;

  private Date endDate;

  @CreatedDate
  private Date createdAt;

  @LastModifiedDate
  private Date updatedAt;

  @Linked(model = User.class)
  @NotNull
  @DBRef
  private User owner;

  @Linked(model = User.class)
  @NotEmpty
  @DBRef
  private List<User> users = new ArrayList<>();

  private List<Keyword> keywords = new ArrayList<>();

  @Linked(model = Assay.class)
  @DBRef(lazy = true)
  private List<Assay> assays = new ArrayList<>();

  private Map<String, Object> attributes = new LinkedHashMap<>();

  private List<ExternalLink> externalLinks = new ArrayList<>();

  private List<StudyRelationship> studyRelationships = new ArrayList<>();

  private Conclusions conclusions;

  private List<Comment> comments = new ArrayList<>();

  @Override
  public boolean isNew() {
    return id == null;
  }

  public Optional<ExternalLink> getExternalLink(String name) {
    return externalLinks.stream().filter(l -> l.getLabel().equals(name)).findFirst();
  }

  public Optional<ExternalLink> getExternalLink(URL url) {
    return externalLinks.stream().filter(l -> l.getUrl().equals(url)).findFirst();
  }

}
