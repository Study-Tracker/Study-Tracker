package com.decibeltx.studytracker.core.events.util;

import com.decibeltx.studytracker.core.events.dto.AssayTypeView;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Activity.Reference;
import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.User;
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
