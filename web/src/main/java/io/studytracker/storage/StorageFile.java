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
import java.util.Date;

public class StorageFile implements StorageObject {

  private String url;

  private String path;

  private String name;

  private Date lastModified;

  private Long size;

  private String fileId;

  private boolean downloadable = false;

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public void setUrl(URL url) {
    this.url = url.toString();
  }

  @Override
  public String getPath() {
    return path;
  }

  @Override
  public void setPath(String path) {
    this.path = path;
  }

  @Override
  public void setPath(Path path) {
    this.path = path.toString();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }

  public boolean isDownloadable() {
    return downloadable;
  }

  public void setDownloadable(boolean canDownload) {
    this.downloadable = canDownload;
  }

  @Override
  public String toString() {
    return "StorageFile{" +
        "url='" + url + '\'' +
        ", path='" + path + '\'' +
        ", name='" + name + '\'' +
        ", lastModified=" + lastModified +
        ", size=" + size +
        ", fileId='" + fileId + '\'' +
        '}';
  }
}
