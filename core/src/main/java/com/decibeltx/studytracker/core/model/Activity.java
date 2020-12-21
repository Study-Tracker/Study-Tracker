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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activity")
public class Activity {

  @Id
  private String id;

  private Reference reference;

  private String referenceId;

  private EventType eventType;

  private Map<String, Object> data = new HashMap<>();

  @Linked(model = User.class)
  @NotNull
  @DBRef
  private User user;

  private Date date;

  /* Getters and Setters */

  public User getUser() {
    return user;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Reference getReference() {
    return reference;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setReference(Reference reference) {
    this.reference = reference;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public Date getDate() {
    return date;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  @JsonProperty("triggeredBy")
  public String triggeredBy() {
    return user.getUsername();
  }

  public enum Reference {
    STUDY,
    ASSAY,
    PROGRAM,
    USER,
    ASSAY_TYPE
  }

}
