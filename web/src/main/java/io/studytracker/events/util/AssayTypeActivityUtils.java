package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.AssayType;
import io.studytracker.model.User;
import java.util.Collections;
import java.util.Date;

public class AssayTypeActivityUtils {

  public static Activity fromNewAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_ASSAY_TYPE);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(
        Collections.singletonMap("assayType", EntityViewUtils.createAssayTypeView(assayType)));
    return activity;
  }

  public static Activity fromUpdatedAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.UPDATED_ASSAY_TYPE);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(
        Collections.singletonMap("assayType", EntityViewUtils.createAssayTypeView(assayType)));
    return activity;
  }

  public static Activity fromDeletedAssayType(AssayType assayType, User triggeredBy) {
    Activity activity = new Activity();
    activity.setEventType(EventType.DELETED_ASSAY_TYPE);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(
        Collections.singletonMap("assayType", EntityViewUtils.createAssayTypeView(assayType)));
    return activity;
  }
}
