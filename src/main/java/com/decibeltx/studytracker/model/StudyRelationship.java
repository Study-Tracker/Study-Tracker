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
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
public class StudyRelationship {

  private Type type;

  @DBRef(lazy = true)
  @JsonIgnore
  private Study study;

  @Transient
  private String studyId;

  public StudyRelationship() {
  }

  @PersistenceConstructor
  public StudyRelationship(Type type, Study study) {
    this.type = type;
    this.study = study;
    this.studyId = study.getCode();
  }

  public StudyRelationship(Type type, String studyId) {
    this.type = type;
    this.studyId = studyId;
  }

  public StudyRelationship(Type type, Study study, String studyId) {
    this.type = type;
    this.study = study;
    this.studyId = studyId;
  }

  public enum Type {

    IS_RELATED_TO,
    IS_PARENT_OF,
    IS_CHILD_OF,
    IS_BLOCKING,
    IS_BLOCKED_BY,
    IS_PRECEDED_BY,
    IS_SUCCEEDED_BY;

    public static Type getInverse(Type type) {
      switch (type) {
        case IS_RELATED_TO:
          return IS_RELATED_TO;
        case IS_PARENT_OF:
          return IS_CHILD_OF;
        case IS_CHILD_OF:
          return IS_PARENT_OF;
        case IS_BLOCKING:
          return IS_BLOCKED_BY;
        case IS_BLOCKED_BY:
          return IS_BLOCKING;
        case IS_PRECEDED_BY:
          return IS_SUCCEEDED_BY;
        case IS_SUCCEEDED_BY:
          return IS_PRECEDED_BY;
        default:
          return IS_RELATED_TO;
      }
    }

  }

}
