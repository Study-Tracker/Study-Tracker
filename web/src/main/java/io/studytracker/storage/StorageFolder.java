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

package io.studytracker.storage;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Transient;

public class StorageFolder implements StorageObject {

  @Transient private StorageFolder parentFolder;

  private String url;

  private String path;

  private String name;

  private Date lastModified;

  private Long totalSize;

  private String folderId;

  private boolean downloadable = false;

  @Transient private List<StorageFolder> subFolders = new ArrayList<>();

  @Transient private List<StorageFile> files = new ArrayList<>();

  public StorageFolder getParentFolder() {
    return parentFolder;
  }

  public void setParentFolder(StorageFolder parentFolder) {
    this.parentFolder = parentFolder;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setUrl(URL url) {
    this.url = url.toString();
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public void setPath(Path path) {
    this.path = path.toString();
  }

  @Override
  public void setPath(String path) {
    this.path = path;
  }

  public List<StorageFolder> getSubFolders() {
    return subFolders;
  }

  public void setSubFolders(List<StorageFolder> subFolders) {
    this.subFolders = subFolders;
  }

  public List<StorageFile> getFiles() {
    return files;
  }

  public void setFiles(List<StorageFile> files) {
    this.files = files;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(Long totalSize) {
    this.totalSize = totalSize;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public void addFile(StorageFile file) {
    files.add(file);
  }

  public void addSubfolder(StorageFolder folder) {
    subFolders.add(folder);
  }

  public boolean isDownloadable() {
    return downloadable;
  }

  public void setDownloadable(boolean downloadable) {
    this.downloadable = downloadable;
  }

  @Override
  public String toString() {
    return "StorageFolder{" +
        "parentFolder=" + parentFolder +
        ", url='" + url + '\'' +
        ", path='" + path + '\'' +
        ", name='" + name + '\'' +
        ", subFolders=" + subFolders +
        ", files=" + files +
        ", lastModified=" + lastModified +
        ", totalSize=" + totalSize +
        ", folderId='" + folderId + '\'' +
        '}';
  }
}
