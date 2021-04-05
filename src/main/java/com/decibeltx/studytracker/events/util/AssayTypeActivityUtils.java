package com.decibeltx.studytracker.events.util;

import com.decibeltx.studytracker.events.dto.AssayTypeView;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Activity.Reference;
import com.decibeltx.studytracker.model.AssayType;
import com.decibeltx.studytracker.model.EventType;
import com.decibeltx.studytracker.model.User;
import java.util.Collections;
import java.util.Date;

public class AssayTypeActivityUtils {

  public static Activity fromNewAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_ASSAY_TYPE);
    activity.setReference(Reference.ASSAY_TYPE);
    activity.setReferenceId(assayType.getId());
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("assayType", AssayTypeView.from(assayType)));
    return activity;
  }

  public static Activity fromUpdatedAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.UPDATED_ASSAY_TYPE);
    activity.setReference(Reference.ASSAY_TYPE);
    activity.setReferenceId(assayType.getId());
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("assayType", AssayTypeView.from(assayType)));
    return activity;
  }

  public static Activity fromDeletedAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.DELETED_ASSAY_TYPE);
    activity.setReference(Reference.ASSAY_TYPE);
    activity.setReferenceId(assayType.getId());
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("assayType", AssayTypeView.from(assayType)));
    return activity;
  }

}
