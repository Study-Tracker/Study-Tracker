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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Deprecated
@Entity
@Table(name = "file_store_folders")
public class FileStoreFolder implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "file_storage_location_id", nullable = false)
  private FileStorageLocation fileStorageLocation;

  @Column(name = "url", length = 1024)
  private String url;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false, length = 1024)
  private String path;

  @Column(name = "reference_id")
  private String referenceId;

  public static FileStoreFolder from(FileStorageLocation location, StorageFolder storageFolder) {
    FileStoreFolder f = new FileStoreFolder();
    f.setFileStorageLocation(location);
    f.setName(storageFolder.getName());
    f.setPath(storageFolder.getPath());
    f.setUrl(storageFolder.getUrl());
    return f;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  public FileStorageLocation getFileStorageLocation() {
    return fileStorageLocation;
  }

  public void setFileStorageLocation(FileStorageLocation fileStorageLocation) {
    this.fileStorageLocation = fileStorageLocation;
  }
}
