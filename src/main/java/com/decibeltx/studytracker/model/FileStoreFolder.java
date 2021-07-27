package com.decibeltx.studytracker.model;

import com.decibeltx.studytracker.storage.StorageFolder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file_store_folders")
public class FileStoreFolder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "url", length = 1034)
  private String url;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false)
  private String path;

  @Column(name = "reference_id")
  private String referenceId;

  public static FileStoreFolder from(StorageFolder storageFolder) {
    FileStoreFolder f = new FileStoreFolder();
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
}
