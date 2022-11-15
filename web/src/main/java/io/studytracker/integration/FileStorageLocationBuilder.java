package io.studytracker.integration;

import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.IntegrationInstance;
import io.studytracker.storage.StorageLocationType;
import io.studytracker.storage.StoragePermissions;
import org.springframework.util.Assert;

public class FileStorageLocationBuilder {

  private FileStorageLocation location;

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
