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

import io.studytracker.storage.StorageFolder;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "storage_drive_folders")
@EntityListeners(AuditingEntityListener.class)
public class StorageDriveFolder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
}
