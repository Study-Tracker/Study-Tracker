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
