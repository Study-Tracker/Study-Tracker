/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.egnyte.entity;

import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class EgnyteFolder implements EgnyteObject, StorageFolder {

  @JsonProperty("name")
  private String name;

  @JsonProperty("path")
  private String path;

  @JsonProperty("is_folder")
  private boolean isFolder;

  @JsonProperty("lastModified")
  private Date lastModified;

  @JsonProperty("uploaded")
  private Date uploaded;

  @JsonProperty("count")
  private Long count;

  @JsonProperty("offset")
  private Long offset;

  @JsonProperty("folder_id")
  private String folderId;

  @JsonProperty("total_count")
  private Long totalCount;

  @JsonProperty("parent_id")
  private String parentId;

  @JsonProperty("public_links")
  private String publicLinks;

  @JsonProperty("restrict_move_delete")
  private Boolean restrictMoveDelete;

  @JsonProperty("allow_links")
  private Boolean allowLinks;

  @Transient
  private List<EgnyteFolder> subFolders = new ArrayList<>();

  @JsonProperty("files")
  @Transient
  private List<EgnyteFile> files = new ArrayList<>();

  private String url;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
  public boolean isFolder() {
    return isFolder;
  }

  public void setFolder(boolean folder) {
    isFolder = folder;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public Date getUploaded() {
    return uploaded;
  }

  public void setUploaded(Date uploaded) {
    this.uploaded = uploaded;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }

  public Long getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Long totalCount) {
    this.totalCount = totalCount;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getPublicLinks() {
    return publicLinks;
  }

  public void setPublicLinks(String publicLinks) {
    this.publicLinks = publicLinks;
  }

  public Boolean getRestrictMoveDelete() {
    return restrictMoveDelete;
  }

  public void setRestrictMoveDelete(Boolean restrictMoveDelete) {
    this.restrictMoveDelete = restrictMoveDelete;
  }

  public Boolean getAllowLinks() {
    return allowLinks;
  }

  public void setAllowLinks(Boolean allowLinks) {
    this.allowLinks = allowLinks;
  }

  @JsonProperty("subFolders")
  @Override
  public List<EgnyteFolder> getSubFolders() {
    return subFolders;
  }

  @JsonProperty("folders")
  public void setSubFolders(
      List<EgnyteFolder> subFolders) {
    this.subFolders = subFolders;
  }

  @Override
  public List<EgnyteFile> getFiles() {
    return files;
  }

  public void setFiles(
      List<EgnyteFile> files) {
    this.files = files;
  }

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
  public void setPath(Path path) {
    this.path = path.toString();
  }
}
