package com.decibeltx.studytracker.core.events;

import com.decibeltx.studytracker.core.model.Activity;
import org.springframework.context.ApplicationEvent;

public class StudyTrackerEvent extends ApplicationEvent {

  private final Activity activity;

  public StudyTrackerEvent(Object source, Activity activity) {
    super(source);
    this.activity = activity;
  }

  public Activity getActivity() {
    return activity;
  }
}
