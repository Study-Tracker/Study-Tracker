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
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.boot.actuate.audit.listener.AuditListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "local_drive_folders")
@EntityListeners(AuditListener.class)
public class EgnyteDriveFolder implements StorageDriveFolderDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @OneToOne(optional = false, targetEntity = StorageDriveFolder.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "storage_drive_folder_id", nullable = false)
  private StorageDriveFolder storageDriveFolder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "egnyte_drive_id", nullable = false, updatable = false)
  private EgnyteDrive egnyteDrive;

  @Column(name = "folder_id", nullable = false)
  private String folderId;

  @Column(name = "web_url")
  private String webUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private Date updatedAt;

  @Override
  public DriveType getDriveType() {
    return DriveType.EGNYTE;
  }

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public StorageDriveFolder getStorageDriveFolder() {
    return storageDriveFolder;
  }

  public void setStorageDriveFolder(StorageDriveFolder storageDriveFolder) {
    this.storageDriveFolder = storageDriveFolder;
  }

  public EgnyteDrive getEgnyteDrive() {
    return egnyteDrive;
  }

  public void setEgnyteDrive(EgnyteDrive egnyteDrive) {
    this.egnyteDrive = egnyteDrive;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public void setWebUrl(String webUrl) {
    this.webUrl = webUrl;
  }

  @Override
  public String getWebUrl() {
    return webUrl;
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
