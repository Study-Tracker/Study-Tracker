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

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "programs")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs(
    @NamedEntityGraph(
        name = "program-with-attributes",
        attributeNodes = {
          @NamedAttributeNode("createdBy"),
          @NamedAttributeNode("lastModifiedBy"),
          @NamedAttributeNode(value = "notebookFolders", subgraph = "program-notebook-folder-details"),
          @NamedAttributeNode(value = "storageFolders", subgraph = "program-storage-folder-details"),
          @NamedAttributeNode("gitGroups")
        },
        subgraphs = {
          @NamedSubgraph(
              name = "program-storage-folder-details",
              attributeNodes = {@NamedAttributeNode(value ="storageDriveFolder", subgraph = "program-folder-drive")}
          ),
          @NamedSubgraph(
              name = "program-notebook-folder-details",
              attributeNodes = {@NamedAttributeNode("elnFolder")}
          ),
          @NamedSubgraph(
              name = "program-folder-drive",
              attributeNodes = {@NamedAttributeNode("storageDrive")}
          )
        }
      ))
@Getter
@Setter
public class Program extends Model {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "hibernate_sequence"
  )
  @SequenceGenerator(
      name = "hibernate_sequence",
      allocationSize = 1
  )
  private Long id;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false, unique = true)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  @CreatedBy
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by", nullable = false)
  @LastModifiedBy
  private User lastModifiedBy;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @OneToMany(
      mappedBy = "program",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ProgramNotebookFolder> notebookFolders = new HashSet<>();

  @OneToMany(
      mappedBy = "program",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ProgramStorageFolder> storageFolders = new HashSet<>();

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Type(JsonBinaryType.class)
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new LinkedHashMap<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "program_git_groups",
      joinColumns = @JoinColumn(name = "program_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "git_group_id", nullable = false))
  private Set<GitGroup> gitGroups = new HashSet<>();

  @Transient
  private ProgramOptions options = new ProgramOptions();

  public void addAttribute(String key, String value) {
    this.attributes.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }

  public void addNotebookFolder(ELNFolder folder, boolean isPrimary) {
    ProgramNotebookFolder programNotebookFolder = new ProgramNotebookFolder();
    programNotebookFolder.setProgram(this);
    programNotebookFolder.setElnFolder(folder);
    programNotebookFolder.setPrimary(isPrimary);
    this.getNotebookFolders().add(programNotebookFolder);
  }

  public void addNotebookFolder(ELNFolder folder) {
    this.addNotebookFolder(folder, false);
  }

  public void addNotebookFolder(ProgramNotebookFolder folder) {
    this.getNotebookFolders().add(folder);
  }

  public void removeNotebookFolder(ProgramNotebookFolder folder) {
    this.notebookFolders.remove(folder);
  }

  public void addStorageFolder(StorageDriveFolder folder) {
    this.addStorageFolder(folder, false);
  }

  public void addStorageFolder(StorageDriveFolder folder, boolean isPrimary) {
    if (isPrimary) {
      this.storageFolders.forEach(f -> f.setPrimary(false));
    }
    ProgramStorageFolder programStorageFolder = new ProgramStorageFolder();
    programStorageFolder.setProgram(this);
    programStorageFolder.setStorageDriveFolder(folder);
    programStorageFolder.setPrimary(isPrimary);
    this.getStorageFolders().add(programStorageFolder);
  }

  public void removeStorageFolder(ProgramStorageFolder folder) {
    this.storageFolders.remove(folder);
  }

  public void addGitGroup(GitGroup gitGroup) {
    this.gitGroups.add(gitGroup);
  }

  public void removeGitGroup(GitGroup gitGroup) {
    this.gitGroups.remove(gitGroup);
  }

}
