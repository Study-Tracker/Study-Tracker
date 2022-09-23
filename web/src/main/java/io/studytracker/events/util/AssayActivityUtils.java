/*
 * Copyright 2022 the original author or authors.
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
import io.studytracker.model.Assay;
import io.studytracker.model.AssayTask;
import io.studytracker.model.Status;
import io.studytracker.model.User;
import io.studytracker.storage.StorageFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AssayActivityUtils {

  public static Activity fromNewAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.NEW_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.UPDATED_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.DELETED_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromChangedAssayStatus(
      Assay assay, User triggeredBy, Status oldStatus, Status newStatus) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.ASSAY_STATUS_CHANGED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    data.put("oldStatus", oldStatus.toString());
    data.put("newStatus", newStatus.toString());
    activity.setData(data);
    return activity;
  }

  public static Activity fromFileUpload(Assay assay, User triggeredBy, StorageFile storageFile) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.FILE_UPLOADED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    data.put("file", EntityViewUtils.createStorageFileView(storageFile));
    activity.setData(data);
    return activity;
  }

  public static Activity fromTaskAdded(Assay assay, User triggeredBy, AssayTask task) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.ASSAY_TASK_ADDED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    data.put("task", EntityViewUtils.createAssayTaskView(task));
    activity.setData(data);
    return activity;
  }

  public static Activity fromAssayTaskUpdate(Assay assay, User triggeredBy, AssayTask task) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.ASSAY_TASK_UPDATED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    data.put("task", EntityViewUtils.createAssayTaskView(task));
    activity.setData(data);
    return activity;
  }

  public static Activity fromTaskDeleted(Assay assay, User triggeredBy, AssayTask task) {
    Activity activity = new Activity();
    activity.setAssay(assay);
    activity.setEventType(EventType.ASSAY_TASK_DELETED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", EntityViewUtils.createAssayView(assay));
    data.put("task", EntityViewUtils.createAssayTaskView(task));
    activity.setData(data);
    return activity;
  }
}
