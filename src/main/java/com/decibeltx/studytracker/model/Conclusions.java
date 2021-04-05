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

package com.decibeltx.studytracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class Conclusions implements Persistable<String> {

  @Id
  private String id;

  @NotNull
  private String content;

  @Linked(model = User.class)
  @NotNull
  @DBRef
  private User createdBy;

  @Linked(model = User.class)
  @DBRef
  private User lastModifiedBy;

  private Date createdAt;

  private Date updatedAt;

  @Override
  @JsonIgnore
  public boolean isNew() {
    return id == null;
  }
}
