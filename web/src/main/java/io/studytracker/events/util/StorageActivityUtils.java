package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.FileStorageLocation;
import io.studytracker.model.User;

public class StorageActivityUtils {

  public static Activity fromNewStorageLocation(FileStorageLocation location, User triggeredBy) {
    return Activity.builder(EventType.NEW_STORAGE_LOCATION, triggeredBy)
        .addData("location", EntityViewUtils.createStorageLocationView(location))
        .build();
  }

  public static Activity fromUpdatedStorageLocation(FileStorageLocation location, User triggeredBy) {
    return Activity.builder(EventType.UPDATED_STORAGE_LOCATION, triggeredBy)
        .addData("location", EntityViewUtils.createStorageLocationView(location))
        .build();
  }

  public static Activity fromDeletedStorageLocation(FileStorageLocation location, User triggeredBy) {
    return Activity.builder(EventType.DELETED_STORAGE_LOCATION, triggeredBy)
        .addData("location", EntityViewUtils.createStorageLocationView(location))
        .build();
  }

}
