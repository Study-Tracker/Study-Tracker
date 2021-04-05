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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.nio.file.Path;
import java.util.Date;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class EgnyteFile implements EgnyteObject {

  @JsonProperty("name")
  private String name;

  @JsonProperty("path")
  private String path;

  @JsonProperty("checksum")
  private String checksum;

  @JsonProperty("size")
  private Long size;

  @JsonProperty("locked")
  private Boolean locked;

  @JsonProperty("entry_id")
  private String entryId;

  @JsonProperty("group_id")
  private String groupId;

  @JsonProperty("last_modified")
  private Date lastModified;

  @JsonProperty("uploaded_by")
  private String uploadedBy;

  @JsonProperty("num_versions")
  private Long numVersions;

  @JsonProperty("parent_id")
  private String parentId;

  private String url;

  @Override
  public boolean isFolder() {
    return false;
  }

  public void setUrl(URL url) {
    this.url = url.toString();
  }

  public void setPath(Path path) {
    this.path = path.toString();
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

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  public String getEntryId() {
    return entryId;
  }

  public void setEntryId(String entryId) {
    this.entryId = entryId;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public String getUploadedBy() {
    return uploadedBy;
  }

  public void setUploadedBy(String uploadedBy) {
    this.uploadedBy = uploadedBy;
  }

  public Long getNumVersions() {
    return numVersions;
  }

  public void setNumVersions(Long numVersions) {
    this.numVersions = numVersions;
  }

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
