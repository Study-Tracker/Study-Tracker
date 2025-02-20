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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
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
@Table(name = "assays", uniqueConstraints = {
    @UniqueConstraint(name = "uq_assay_name", columnNames = {"name", "study_id"}),
    @UniqueConstraint(name = "uq_assay_code", columnNames = {"code", "study_id"})
})
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "assay-summary",
      attributeNodes = {
        @NamedAttributeNode("assayType"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("users")
      }),
  @NamedEntityGraph(
      name = "assay-with-attributes",
      attributeNodes = {
        @NamedAttributeNode(value = "assayType", subgraph = "assay-type-details"),
        @NamedAttributeNode(value = "notebookFolders", subgraph = "assay-notebook-folder-details"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModifiedBy"),
        @NamedAttributeNode(value = "tasks", subgraph = "assay-task-details"),
        @NamedAttributeNode("users"),
        @NamedAttributeNode("study"),
        @NamedAttributeNode(value = "storageFolders", subgraph = "assay-storage-folder-details"),
        @NamedAttributeNode("gitRepositories")
      },
      subgraphs = {
        @NamedSubgraph(
            name = "assay-type-details",
            attributeNodes = {@NamedAttributeNode("fields"), @NamedAttributeNode("tasks")}),
        @NamedSubgraph(
            name = "assay-task-details",
            attributeNodes = {
              @NamedAttributeNode("createdBy"),
              @NamedAttributeNode("lastModifiedBy"),
              @NamedAttributeNode("assignedTo")
            }),
        @NamedSubgraph(
            name = "assay-storage-folder-details",
            attributeNodes = {@NamedAttributeNode("storageDriveFolder")}
        ),
        @NamedSubgraph(
            name = "assay-notebook-folder-details",
            attributeNodes = {@NamedAttributeNode("elnFolder")}
        )
      }),
  @NamedEntityGraph(
      name = "assay-with-parents",
      attributeNodes = {
        @NamedAttributeNode("assayType"),
        @NamedAttributeNode("owner"),
        @NamedAttributeNode(value = "study", subgraph = "study-summary")
      },
      subgraphs = {
        @NamedSubgraph(
            name = "study-summary",
            attributeNodes = {@NamedAttributeNode("program"), @NamedAttributeNode("collaborator")})
      })
})
@Getter
@Setter
public class Assay extends Model {

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

  @OneToMany(
      mappedBy = "assay",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<AssayNotebookFolder> notebookFolders = new HashSet<>();

  @OneToMany(
      mappedBy = "assay",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<AssayStorageFolder> storageFolders = new HashSet<>();

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
  @JoinTable(
      name = "assay_users",
      joinColumns = @JoinColumn(name = "assay_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "user_id", nullable = false))
  private Set<User> users = new HashSet<>();

  @Type(type = "json")
  @Column(name = "fields", columnDefinition = "json")
  private Map<String, Object> fields = new LinkedHashMap<>();

  @Type(type = "json")
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new LinkedHashMap<>();

  @OneToMany(
      mappedBy = "assay",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<AssayTask> tasks = new HashSet<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "assay_git_repositories",
      joinColumns = @JoinColumn(name = "assay_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "git_repository_id", nullable = false))
  private Set<GitRepository> gitRepositories = new HashSet<>();

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

  public void setTasks(Set<AssayTask> tasks) {
    for (AssayTask task : tasks) {
      task.setAssay(this);
    }
    this.tasks = tasks;
  }

  public void addStorageFolder(AssayStorageFolder folder) {
    this.storageFolders.add(folder);
  }

  public void addStorageFolder(StorageDriveFolder folder) {
    addStorageFolder(folder, false);
  }

  public void addStorageFolder(StorageDriveFolder folder, boolean isPrimary) {
    if (isPrimary) {
      this.storageFolders.forEach(f -> f.setPrimary(false));
    }
    AssayStorageFolder assayStorageFolder = new AssayStorageFolder();
    assayStorageFolder.setAssay(this);
    assayStorageFolder.setStorageDriveFolder(folder);
    assayStorageFolder.setPrimary(isPrimary);
    this.getStorageFolders().add(assayStorageFolder);
  }

  public void removeStorageFolder(AssayStorageFolder folder) {
    this.storageFolders.remove(folder);
  }

  public void addGitRepository(GitRepository gitRepository) {
    this.gitRepositories.add(gitRepository);
  }

  public void removeGitRepository(GitRepository gitRepository) {
    this.gitRepositories.remove(gitRepository);
  }

  public void addNotebookFolder(AssayNotebookFolder folder) {
    folder.setAssay(this);
    this.notebookFolders.add(folder);
  }

  public void addNotebookFolder(ELNFolder elnFolder, boolean isPrimary) {
    if (isPrimary) {
      this.notebookFolders.forEach(f -> f.setPrimary(false));
    }
    AssayNotebookFolder assayNotebookFolder = new AssayNotebookFolder();
    assayNotebookFolder.setAssay(this);
    assayNotebookFolder.setElnFolder(elnFolder);
    assayNotebookFolder.setPrimary(isPrimary);
    this.getNotebookFolders().add(assayNotebookFolder);
  }

  public void addNotebookFolder(ELNFolder elnFolder) {
    this.addNotebookFolder(elnFolder, false);
  }

  public void removeNotebookFolder(AssayNotebookFolder folder) {
    this.notebookFolders.remove(folder);
  }

}
