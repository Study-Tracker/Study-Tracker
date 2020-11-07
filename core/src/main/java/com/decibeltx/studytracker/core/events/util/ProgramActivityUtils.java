package com.decibeltx.studytracker.core.events.util;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Activity.Reference;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProgramActivityUtils {

  public static Activity fromNewProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.PROGRAM);
    activity.setReferenceId(program.getId());
    activity.setEventType(EventType.NEW_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", program);
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.PROGRAM);
    activity.setReferenceId(program.getId());
    activity.setEventType(EventType.UPDATED_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", program);
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.PROGRAM);
    activity.setReferenceId(program.getId());
    activity.setEventType(EventType.DELETED_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", program);
    activity.setData(data);
    return activity;
  }

}
