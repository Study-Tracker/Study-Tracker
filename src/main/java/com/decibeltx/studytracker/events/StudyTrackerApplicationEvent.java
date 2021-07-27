package com.decibeltx.studytracker.events;

import com.decibeltx.studytracker.model.Activity;
import java.util.Date;
import java.util.Map;
import org.springframework.context.ApplicationEvent;

public class StudyTrackerApplicationEvent extends ApplicationEvent implements StudyTrackerEvent {

  private final Activity activity;

  public StudyTrackerApplicationEvent(Object source, Activity activity) {
    super(source);
    this.activity = activity;
  }

  public Activity getActivity() {
    return activity;
  }

  @Override
  public EventType getEventType() {
    return activity.getEventType();
  }

  @Override
  public Map<String, Object> getData() {
    return activity.getData();
  }

  @Override
  public String getTriggeredBy() {
    return activity.getUser().getUsername();
  }

  @Override
  public Date getDate() {
    return activity.getDate();
  }
}
