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

import com.decibeltx.studytracker.events.EventType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "activity")
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "activity-with-user",
        attributeNodes = { @NamedAttributeNode("user") }),
    @NamedEntityGraph(name = "activity-details", attributeNodes = {
        @NamedAttributeNode("user") ,
        @NamedAttributeNode("program"),
        @NamedAttributeNode("study"),
        @NamedAttributeNode("assay")
    })
})
public class Activity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "program_id")
  private Program program;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id")
  private Study study;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_id")
  private Assay assay;

  @Column(name = "event_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private EventType eventType;

  @Column(name = "data", columnDefinition = "json")
  @Type(type = "json")
  private Map<String, Object> data = new HashMap<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  public Activity() { }

  public Activity(EventType type, User user) {
    this.eventType = type;
    this.user = user;
    this.date = new Date();
  }

  @JsonProperty("triggeredBy")
  public String triggeredBy() {
    return user.getUsername();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public Assay getAssay() {
    return assay;
  }

  public void setAssay(Assay assay) {
    this.assay = assay;
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void addData(String key, Object value) {
    this.data.put(key, value);
  }

  public void removeData(String key) {
    this.data.remove(key);
  }

}
