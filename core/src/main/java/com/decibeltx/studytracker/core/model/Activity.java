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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activity")
public class Activity {

  @Id
  private String id;

  @Linked(model = Study.class)
  @DBRef
  @NotNull
  @JsonIgnore
  private Study study;

  @Linked(model = Assay.class)
  @DBRef
  @JsonIgnore
  private Assay assay;

  @Linked(model = User.class)
  @NotNull
  @DBRef
  @JsonIgnore
  private User user;

  private String action;

  private Date date;

  private Object data;

  @JsonProperty("studyCode")
  public String getStudyCode() {
    return study != null ? study.getCode() : null;
  }

  @JsonProperty("assayCode")
  public String getAssayCode() {
    return assay != null ? assay.getCode() : null;
  }

  @JsonProperty("userAccountName")
  public String getUserAccountName() {
    return user != null ? user.getAccountName() : null;
  }

  @JsonProperty("userDisplayName")
  public String getUserDisplayName() {
    return user != null ? user.getDisplayName() : null;
  }

  public String getId() {
    return id;
  }

  public Study getStudy() {
    return study;
  }

  public Assay getAssay() {
    return assay;
  }

  public User getUser() {
    return user;
  }

  public String getAction() {
    return action;
  }

  public Date getDate() {
    return date;
  }

  public Object getData() {
    return data;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public void setAssay(Assay assay) {
    this.assay = assay;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setData(Object data) {
    this.data = data;
  }
}
