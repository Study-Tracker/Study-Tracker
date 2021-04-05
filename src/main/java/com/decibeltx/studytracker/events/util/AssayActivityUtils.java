package com.decibeltx.studytracker.events.util;

import com.decibeltx.studytracker.events.dto.AssayView;
import com.decibeltx.studytracker.events.dto.StorageFileView;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Activity.Reference;
import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.EventType;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Task;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.storage.StorageFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AssayActivityUtils {

  public static Activity fromNewAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.NEW_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.UPDATED_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedAssay(Assay assay, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.DELETED_ASSAY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    activity.setData(data);
    return activity;
  }

  public static Activity fromChangedAssayStatus(Assay assay, User triggeredBy, Status oldStatus,
      Status newStatus) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.ASSAY_STATUS_CHANGED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    data.put("oldStatus", oldStatus);
    data.put("newStatus", newStatus);
    activity.setData(data);
    return activity;
  }

  public static Activity fromFileUpload(Assay assay, User triggeredBy, StorageFile storageFile) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.FILE_UPLOADED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    data.put("file", StorageFileView.from(storageFile));
    activity.setData(data);
    return activity;
  }

  public static Activity fromTaskAdded(Assay assay, User triggeredBy, Task task) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.ASSAY_TASK_ADDED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    data.put("task", task);
    activity.setData(data);
    return activity;
  }

  public static Activity fromTaskUpdate(Assay assay, User triggeredBy, Task task) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.ASSAY_TASK_UPDATED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    data.put("task", task);
    activity.setData(data);
    return activity;
  }

  public static Activity fromTaskDeleted(Assay assay, User triggeredBy, Task task) {
    Activity activity = new Activity();
    activity.setReference(Reference.ASSAY);
    activity.setReferenceId(assay.getId());
    activity.setEventType(EventType.ASSAY_TASK_DELETED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("assay", AssayView.from(assay));
    data.put("task", task);
    activity.setData(data);
    return activity;
  }

}
