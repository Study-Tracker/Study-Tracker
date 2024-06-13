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

package io.studytracker.model;

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
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "studies",
    indexes = {
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
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("keywords"),
        @NamedAttributeNode("users")
      }),
  @NamedEntityGraph(
      name = "study-with-attributes",
      attributeNodes = {
        @NamedAttributeNode("program"),
        @NamedAttributeNode("collaborator"),
        @NamedAttributeNode(value = "notebookFolders", subgraph = "study-notebook-folder-details"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModifiedBy"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("users"),
        @NamedAttributeNode("keywords"),
        @NamedAttributeNode("externalLinks"),
        @NamedAttributeNode("conclusions"),
        @NamedAttributeNode("comments"),
        @NamedAttributeNode("studyRelationships"),
        @NamedAttributeNode(value = "storageFolders", subgraph = "study-storage-folder-details"),
        @NamedAttributeNode("gitRepositories")
      },
    subgraphs = {
          @NamedSubgraph(
              name = "study-storage-folder-details",
              attributeNodes = {
                  @NamedAttributeNode(value = "storageDriveFolder", subgraph = "storage-folder-details")
              }
          ),
          @NamedSubgraph(
              name = "storage-folder-details",
              attributeNodes = {
                  @NamedAttributeNode("storageDrive")
              }
          ),
          @NamedSubgraph(
              name = "study-notebook-folder-details",
              attributeNodes = {@NamedAttributeNode("elnFolder")}
          )
    }
  )
})
@Getter
@Setter
public class Study extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "external_code")
  private String externalCode;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "name", nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "program_id", nullable = false)
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

  @OneToMany(
      mappedBy = "study",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<StudyNotebookFolder> notebookFolders = new HashSet<>();

  @OneToMany(
      mappedBy = "study",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<StudyStorageFolder> storageFolders = new HashSet<>();

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
  @JoinTable(
      name = "study_users",
      joinColumns = @JoinColumn(name = "study_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
  private Set<User> users = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "study_keywords",
      joinColumns = @JoinColumn(name = "study_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "keyword_id", nullable = false))
  private Set<Keyword> keywords = new HashSet<>();

  @OneToMany(
      mappedBy = "study",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @JsonIgnore
  private Set<Assay> assays = new HashSet<>();

  @Type(type = "json")
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new LinkedHashMap<>();

  @OneToMany(
      mappedBy = "study",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ExternalLink> externalLinks = new HashSet<>();

  @OneToMany(
      mappedBy = "sourceStudy",
      fetch = FetchType.LAZY,
      cascade = CascadeType.PERSIST,
      orphanRemoval = true)
  private Set<StudyRelationship> studyRelationships = new HashSet<>();

  @OneToOne(
      mappedBy = "study",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private StudyConclusions conclusions;

  @OneToMany(
      mappedBy = "study",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Comment> comments = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "study_git_repositories",
      joinColumns = @JoinColumn(name = "study_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "git_repository_id", nullable = false))
  private Set<GitRepository> gitRepositories = new HashSet<>();

  @Transient
  private StudyOptions options = new StudyOptions();

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

  public void setAssays(Set<Assay> assays) {
    for (Assay assay : assays) {
      assay.setStudy(this);
    }
    this.assays = assays;
  }

  public void setExternalLinks(Set<ExternalLink> externalLinks) {
    for (ExternalLink link : externalLinks) {
      link.setStudy(this);
    }
    this.externalLinks = externalLinks;
  }

  public void setConclusions(StudyConclusions conclusions) {
    if (conclusions != null) conclusions.setStudy(this);
    this.conclusions = conclusions;
  }

  public void setComments(Set<Comment> comments) {
    for (Comment comment : comments) {
      comment.setStudy(this);
    }
    this.comments = comments;
  }

  public boolean hasAttribute(String key) {
    return this.attributes.containsKey(key);
  }

  public String getAttribute(String key) {
    return this.attributes.get(key);
  }

  public void setAttribute(String key, String value) {
    this.attributes.put(key, value);
  }

  public void addStudyStorageFolder(StudyStorageFolder folder) {
    this.storageFolders.add(folder);
  }

  public void addStorageFolder(StorageDriveFolder storageDriveFolder) {
    this.addStorageFolder(storageDriveFolder, false);
  }

  public void addStorageFolder(StorageDriveFolder folder, boolean isPrimary) {
    if (isPrimary) {
      this.storageFolders.forEach(f -> f.setPrimary(false));
    }
    StudyStorageFolder studyStorageFolder = new StudyStorageFolder();
    studyStorageFolder.setStudy(this);
    studyStorageFolder.setStorageDriveFolder(folder);
    studyStorageFolder.setPrimary(isPrimary);
    this.getStorageFolders().add(studyStorageFolder);
  }

  public void removeStudyStorageFolder(StudyStorageFolder folder) {
    this.storageFolders.remove(folder);
  }

  public void addNotebookFolder(StudyNotebookFolder folder) {
    folder.setStudy(this);
    this.notebookFolders.add(folder);
  }

  public void addNotebookFolder(ELNFolder elnFolder, boolean isPrimary) {
    if (isPrimary) {
      this.notebookFolders.forEach(f -> f.setPrimary(false));
    }
    StudyNotebookFolder studyNotebookFolder = new StudyNotebookFolder();
    studyNotebookFolder.setStudy(this);
    studyNotebookFolder.setElnFolder(elnFolder);
    studyNotebookFolder.setPrimary(isPrimary);
    this.getNotebookFolders().add(studyNotebookFolder);
  }

  public void addNotebookFolder(ELNFolder elnFolder) {
    this.addNotebookFolder(elnFolder, false);
  }

  public void removeNotebookFolder(StudyNotebookFolder folder) {
    this.notebookFolders.remove(folder);
  }

  public void addGitRepository(GitRepository gitRepository) {
    this.gitRepositories.add(gitRepository);
  }

  public void removeGitRepository(GitRepository gitRepository) {
    this.gitRepositories.remove(gitRepository);
  }
}
