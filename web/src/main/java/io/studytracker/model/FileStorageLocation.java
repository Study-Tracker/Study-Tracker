/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
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
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * The FileStorageLocation entity should be thought of as a "mount point" for a storage system
 *   connected-to via a {@link IntegrationInstance} (e.g. an Egnyte folder, a local folder, a S3
 *   bucket, etc.). This record defines the permissions and root directory of the storage system
 *   accessible to Study Tracker. A {@code FileStorageLocation} entities should not overlap in their
 *   file system paths (e.g. having one location root path defined as a subfolder of another).
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Entity
@Table(name = "file_storage_locations")
@EntityListeners(AuditingEntityListener.class)
public class FileStorageLocation implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "integration_instance_id", nullable = false, updatable = false)
  private IntegrationInstance integrationInstance;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private StorageLocationType type;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false)
  private String rootFolderPath;

  @Column(name = "reference_id")
  private String referenceId;

  @Column(name = "url")
  private String url;

  @Column(name = "permissions", nullable = false)
  @Enumerated(EnumType.STRING)
  private StoragePermissions permissions;

  @Column(name = "default_study_location")
  private boolean defaultStudyLocation = false;

  @Column(name = "default_data_location")
  private boolean defaultDataLocation = false;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IntegrationInstance getIntegrationInstance() {
    return integrationInstance;
  }

  public void setIntegrationInstance(IntegrationInstance integrationInstance) {
    this.integrationInstance = integrationInstance;
  }

  public StorageLocationType getType() {
    return type;
  }

  public void setType(StorageLocationType type) {
    this.type = type;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRootFolderPath() {
    return rootFolderPath;
  }

  public void setRootFolderPath(String rootFolderPath) {
    this.rootFolderPath = rootFolderPath;
  }

  public StoragePermissions getPermissions() {
    return permissions;
  }

  public void setPermissions(StoragePermissions permissions) {
    this.permissions = permissions;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isDefaultStudyLocation() {
    return defaultStudyLocation;
  }

  public void setDefaultStudyLocation(boolean defaultStudyLocation) {
    this.defaultStudyLocation = defaultStudyLocation;
  }

  public boolean isDefaultDataLocation() {
    return defaultDataLocation;
  }

  public void setDefaultDataLocation(boolean defaultDataLocation) {
    this.defaultDataLocation = defaultDataLocation;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
