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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
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
@Table(name = "studies", indexes = {
    @Index(name = "idx_study_code", columnList = "code"),
    @Index(name = "idx_study_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "study-summary",
        attributeNodes = {
            @NamedAttributeNode("program"),
            @NamedAttributeNode("collaborator"),
            @NamedAttributeNode("notebookFolder"),
            @NamedAttributeNode("storageFolder"),
            @NamedAttributeNode("owner")
        }
    ),
    @NamedEntityGraph(
        name = "study-with-attributes",
        attributeNodes = {
            @NamedAttributeNode("program"),
            @NamedAttributeNode("collaborator"),
            @NamedAttributeNode("notebookFolder"),
            @NamedAttributeNode("storageFolder"),
            @NamedAttributeNode("createdBy"),
            @NamedAttributeNode("lastModifiedBy"),
            @NamedAttributeNode("owner"),
            @NamedAttributeNode("users"),
            @NamedAttributeNode("keywords"),
            @NamedAttributeNode("externalLinks"),
            @NamedAttributeNode("conclusions"),
            @NamedAttributeNode("comments"),
            @NamedAttributeNode("studyRelationships")
        }
    )
})
public class Study {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "code", nullable = false, unique = true, updatable = false)
  private String code;

  @Column(name = "external_code")
  private String externalCode;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "name", nullable = false, updatable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "program_id", nullable = false, updatable = false)
  private Program program;

  @Column(name = "description", nullable = false, columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collaborator_id")
  private Collaborator collaborator;

  @Column(name = "legacy", nullable = false)
  private boolean legacy = false;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "notebook_folder_id")
  private ELNFolder notebookFolder;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "storage_folder_id")
  private FileStoreFolder storageFolder;

  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @LastModifiedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by", nullable = false)
  private User lastModifiedBy;

  @Column(name = "start_date", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @Column(name = "end_date")
  @Temporal(TemporalType.TIMESTAMP)
  private Date endDate;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner", nullable = false)
  private User owner;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "study_users",
      joinColumns = @JoinColumn(name = "study_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
  private Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "study_keywords",
      joinColumns = @JoinColumn(name = "study_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "keyword_id", nullable = false))
  private Set<Keyword> keywords = new HashSet<>();

  @OneToMany(mappedBy = "study", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private Set<Assay> assays = new HashSet<>();

  @Type(type = "json")
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new LinkedHashMap<>();

  @OneToMany(mappedBy = "study", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ExternalLink> externalLinks = new HashSet<>();

  @OneToMany(mappedBy = "sourceStudy", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
  private Set<StudyRelationship> studyRelationships = new HashSet<>();

//  @OneToMany(mappedBy = "targetStudy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//  private Set<StudyRelationship> studyRelationships = new HashSet<>();

  @OneToOne(mappedBy = "study", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private StudyConclusions conclusions;

  @OneToMany(mappedBy = "study", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  public void addUser(User user) {
    this.users.add(user);
  }

  public void removeUser(User user) {
    this.users.remove(user);
  }

  public void removeUser(Long id) {
    this.users.removeIf(d -> d.getId().equals(id));
  }

  public void addKeyword(Keyword keyword) {
    this.keywords.add(keyword);
  }

  public void removeKeyword(Keyword keyword) {
    this.keywords.remove(keyword);
  }

  public void removeKeyword(Long id) {
    this.keywords.removeIf(d -> d.getId().equals(id));
  }

  public void addAssay(Assay assay) {
    assay.setStudy(this);
    this.assays.add(assay);
  }

  public void removeAssay(Assay assay) {
    this.assays.remove(assay);
  }

  public void removeAssay(Long id) {
    this.assays.removeIf(d -> d.getId().equals(id));
  }

  public void addAttribute(String key, String value) {
    this.attributes.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }

  public void addExternalLink(ExternalLink link) {
    link.setStudy(this);
    this.externalLinks.add(link);
  }

  public void removeExternalLink(ExternalLink link) {
    this.externalLinks.remove(link);
  }

  public void removeExternalLink(Long id) {
    this.externalLinks.removeIf(d -> d.getId().equals(id));
  }

  public void addStudyRelationship(StudyRelationship relationship) {
    this.studyRelationships.add(relationship);
  }

  public void removeStudyRelationship(StudyRelationship relationship) {
    this.studyRelationships.remove(relationship);
  }

  public void removeStudyRelationship(Long id) {
    this.studyRelationships.removeIf(d -> d.getId().equals(id));
  }

  public void addComment(Comment comment) {
    comment.setStudy(this);
    this.comments.add(comment);
  }

  public void removeComment(Comment comment) {
    this.comments.remove(comment);
  }

  public void removeComment(Long id) {
    this.comments.removeIf(c -> c.getId().equals(id));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getExternalCode() {
    return externalCode;
  }

  public void setExternalCode(String externalCode) {
    this.externalCode = externalCode;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Collaborator getCollaborator() {
    return collaborator;
  }

  public void setCollaborator(Collaborator collaborator) {
    this.collaborator = collaborator;
  }

  public boolean isLegacy() {
    return legacy;
  }

  public void setLegacy(boolean legacy) {
    this.legacy = legacy;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
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

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public Set<Keyword> getKeywords() {
    return keywords;
  }

  public void setKeywords(Set<Keyword> keywords) {
    this.keywords = keywords;
  }

  public Set<Assay> getAssays() {
    return assays;
  }

  public void setAssays(Set<Assay> assays) {
    for (Assay assay: assays) {
      assay.setStudy(this);
    }
    this.assays = assays;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  public Set<ExternalLink> getExternalLinks() {
    return externalLinks;
  }

  public void setExternalLinks(Set<ExternalLink> externalLinks) {
    for (ExternalLink link: externalLinks) {
      link.setStudy(this);
    }
    this.externalLinks = externalLinks;
  }

  public Set<StudyRelationship> getStudyRelationships() {
    return studyRelationships;
  }

  public void setStudyRelationships(Set<StudyRelationship> studyRelationships) {
    this.studyRelationships = studyRelationships;
  }

  public StudyConclusions getConclusions() {
    return conclusions;
  }

  public void setConclusions(StudyConclusions conclusions) {
    if (conclusions != null) conclusions.setStudy(this);
    this.conclusions = conclusions;
  }

  public Set<Comment> getComments() {
    return comments;
  }

  public void setComments(Set<Comment> comments) {
    for (Comment comment: comments) {
      comment.setStudy(this);
    }
    this.comments = comments;
  }
}
