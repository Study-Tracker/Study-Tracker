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

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assays")
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "assay-summary", attributeNodes = {
        @NamedAttributeNode("assayType"),
        @NamedAttributeNode("notebookFolder"),
        @NamedAttributeNode("storageFolder"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("users")
    }),
    @NamedEntityGraph(name = "assay-with-attributes", attributeNodes = {
        @NamedAttributeNode(value = "assayType", subgraph = "assay-type-details"),
        @NamedAttributeNode("notebookFolder"),
        @NamedAttributeNode("storageFolder"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModifiedBy"),
        @NamedAttributeNode(value = "tasks", subgraph = "assay-task-details"),
        @NamedAttributeNode("users")
    }, subgraphs = {
        @NamedSubgraph(name = "assay-type-details", attributeNodes = {
            @NamedAttributeNode("fields"),
            @NamedAttributeNode("tasks")
        }),
        @NamedSubgraph(name = "assay-task-details", attributeNodes = {
            @NamedAttributeNode("createdBy"),
            @NamedAttributeNode("lastModifiedBy"),
            @NamedAttributeNode("assignedTo")
        })
    }),
    @NamedEntityGraph(name = "assay-with-parents", attributeNodes =  {
        @NamedAttributeNode("assayType"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("notebookFolder"),
        @NamedAttributeNode("storageFolder"),
        @NamedAttributeNode(value = "study", subgraph = "study-summary")
    }, subgraphs = {
        @NamedSubgraph(name = "study-summary", attributeNodes = {
            @NamedAttributeNode("program"),
            @NamedAttributeNode("collaborator")
        })
    })
})
public class Assay {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_type_id", nullable = false)
  private AssayType assayType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_id", nullable = false)
  private Study study;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @LastModifiedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by", nullable = false)
  private User lastModifiedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner", nullable = false)
  private User owner;

  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @Column(name = "end_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date endDate;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "notebook_folder_id")
  private ELNFolder notebookFolder;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "storage_folder_id")
  private FileStoreFolder storageFolder;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "assay_users",
      joinColumns = @JoinColumn(name = "assay_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
  private Set<User> users = new HashSet<>();

  @Type(type = "json")
  @Column(name = "fields", columnDefinition = "json")
  private Map<String, Object> fields = new LinkedHashMap<>();

  @Type(type = "json")
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new LinkedHashMap<>();

  @OneToMany(mappedBy = "assay", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AssayTask> tasks = new HashSet<>();

  public void addTask(AssayTask task) {
    task.setAssay(this);
    this.tasks.add(task);
  }

  public void removeTask(AssayTask task) {
    this.tasks.remove(task);
  }

  public void removeTask(Long id) {
    this.tasks.removeIf(t -> t.getId().equals(id));
  }

  public void addField(String key, Object value) {
    this.fields.put(key, value);
  }

  public void removeField(String key) {
    this.fields.remove(key);
  }

  public void addAttribute(String key, String value) {
    this.attributes.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }

  public void addUser(User user) {
    this.users.add(user);
  }

  public void removeUser(User user) {
    this.users.remove(user);
  }

  public void removeUser(Long id) {
    this.users.removeIf(u -> u.getId().equals(id));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public AssayType getAssayType() {
    return assayType;
  }

  public void setAssayType(AssayType assayType) {
    this.assayType = assayType;
  }

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public User getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(User lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public ELNFolder getNotebookFolder() {
    return notebookFolder;
  }

  public void setNotebookFolder(ELNFolder notebookFolder) {
    this.notebookFolder = notebookFolder;
  }

  public FileStoreFolder getStorageFolder() {
    return storageFolder;
  }

  public void setStorageFolder(FileStoreFolder storageFolder) {
    this.storageFolder = storageFolder;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> fields) {
    this.fields = fields;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  public Set<AssayTask> getTasks() {
    return tasks;
  }

  public void setTasks(Set<AssayTask> tasks) {
    for (AssayTask task: tasks) {
      task.setAssay(this);
    }
    this.tasks = tasks;
  }
}
