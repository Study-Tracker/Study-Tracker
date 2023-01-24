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

package io.studytracker.integration;

import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import org.springframework.util.Assert;

public class FileStorageLocationBuilder {

  private final FileStorageLocation location;

  public FileStorageLocationBuilder() {
    location = new FileStorageLocation();
  }

  public FileStorageLocationBuilder integrationInstance(IntegrationInstance integrationInstance) {
    location.setIntegrationInstance(integrationInstance);
    return this;
  }

  public FileStorageLocationBuilder type(StorageLocationType type) {
    location.setType(type);
    return this;
  }

  public FileStorageLocationBuilder name(String name) {
    location.setName(name);
    return this;
  }

  public FileStorageLocationBuilder displayName(String displayName) {
    location.setDisplayName(displayName);
    return this;
  }

  public FileStorageLocationBuilder rootFolderPath(String rootFolderPath) {
    location.setRootFolderPath(rootFolderPath);
    return this;
  }

  public FileStorageLocationBuilder referenceId(String referenceId) {
    location.setReferenceId(referenceId);
    return this;
  }

  public FileStorageLocationBuilder url(String url) {
    location.setUrl(url);
    return this;
  }

  public FileStorageLocationBuilder permissions(StoragePermissions permissions) {
    location.setPermissions(permissions);
    return this;
  }

  public FileStorageLocationBuilder defaultStudyLocation(boolean defaultStudyLocation) {
    location.setDefaultStudyLocation(defaultStudyLocation);
    return this;
  }

  public FileStorageLocationBuilder defaultDataLocation(boolean defaultDataLocation) {
    location.setDefaultDataLocation(defaultDataLocation);
    return this;
  }

  public FileStorageLocation build() {
    try {
      Assert.notNull(location.getIntegrationInstance(), "Integration instance is required");
      Assert.notNull(location.getType(), "Type is required");
      Assert.notNull(location.getName(), "Name is required");
      Assert.notNull(location.getRootFolderPath(), "Root folder path is required");
      Assert.notNull(location.getPermissions(), "Permissions are required");
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalStateException("File storage location is not valid");
    }
    return location;
  }

}
