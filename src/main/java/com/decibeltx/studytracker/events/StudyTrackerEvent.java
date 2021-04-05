package com.decibeltx.studytracker.events;

import com.decibeltx.studytracker.model.Activity;
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
