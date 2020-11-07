package com.decibeltx.studytracker.core.events.util;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Activity.Reference;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.User;
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
    data.put("assay", assay);
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
    data.put("assay", assay);
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
    data.put("assay", assay);
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
    data.put("assay", assay);
    data.put("oldStatus", oldStatus);
    data.put("newStatus", newStatus);
    activity.setData(data);
    return activity;
  }

}
