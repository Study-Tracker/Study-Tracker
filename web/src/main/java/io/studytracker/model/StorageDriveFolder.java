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
import io.studytracker.storage.StorageFolder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "storage_drive_folders")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "storage-drive-folder-details",
    attributeNodes = {
        @NamedAttributeNode("storageDrive")
    }),
})
public class StorageDriveFolder {

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

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "storage_drive_id", nullable = false, updatable = false)
  private StorageDrive storageDrive;

  @Column(name = "path", nullable = false, length = 2048)
  private String path;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "is_browser_root", nullable = false)
  private boolean browserRoot = false;

  @Column(name = "is_study_root", nullable = false)
  private boolean studyRoot = false;

  @Column(name = "is_write_enabled", nullable = false)
  private boolean writeEnabled = false;

  @Column(name = "is_delete_enabled", nullable = false)
  private boolean deleteEnabled = false;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @Type(JsonBinaryType.class)
  @Column(name = "details", columnDefinition = "json")
  private StorageDriveFolderDetails details;

  public static StorageDriveFolder from(StorageDrive drive, StorageFolder f) {
    StorageDriveFolder folder = new StorageDriveFolder();
    folder.setPath(f.getPath());
    folder.setName(f.getName());
    folder.setStorageDrive(drive);
    return folder;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public StorageDrive getStorageDrive() {
    return storageDrive;
  }

  public void setStorageDrive(StorageDrive storageDrive) {
    this.storageDrive = storageDrive;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isBrowserRoot() {
    return browserRoot;
  }

  public void setBrowserRoot(boolean browserRoot) {
    this.browserRoot = browserRoot;
  }

  public boolean isStudyRoot() {
    return studyRoot;
  }

  public void setStudyRoot(boolean studyRoot) {
    this.studyRoot = studyRoot;
  }

  public boolean isWriteEnabled() {
    return writeEnabled;
  }

  public void setWriteEnabled(boolean writeEnabled) {
    this.writeEnabled = writeEnabled;
  }

  public boolean isDeleteEnabled() {
    return deleteEnabled;
  }

  public void setDeleteEnabled(boolean deleteEnabled) {
    this.deleteEnabled = deleteEnabled;
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

  public StorageDriveFolderDetails getDetails() {
    return details;
  }

  public void setDetails(StorageDriveFolderDetails details) {
    this.details = details;
  }
}
