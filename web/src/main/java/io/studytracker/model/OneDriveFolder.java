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

import io.studytracker.model.StorageDrive.DriveType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "onedrive_folders")
@EntityListeners(AuditingEntityListener.class)
public class OneDriveFolder implements StorageDriveFolderDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne(optional = false, targetEntity = StorageDriveFolder.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "storage_drive_folder_id", nullable = false)
  private StorageDriveFolder storageDriveFolder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "onedrive_drive_id", nullable = false, updatable = false)
  private OneDriveDrive oneDriveDrive;

  @Column(name = "folder_id", nullable = false, updatable = false)
  private String folderId;

  @Column(name = "web_url", nullable = false, updatable = false, length = 2048)
  private String webUrl;

  @Column(name = "path", length = 2048)
  private String path;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public StorageDriveFolder getStorageDriveFolder() {
    return storageDriveFolder;
  }

  public void setStorageDriveFolder(StorageDriveFolder storageDriveFolder) {
    this.storageDriveFolder = storageDriveFolder;
  }

  public OneDriveDrive getOneDriveDrive() {
    return oneDriveDrive;
  }

  public void setOneDriveDrive(OneDriveDrive oneDriveDrive) {
    this.oneDriveDrive = oneDriveDrive;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public String getWebUrl() {
    return webUrl;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  @Override
  public DriveType getDriveType() {
    return DriveType.ONEDRIVE;
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
