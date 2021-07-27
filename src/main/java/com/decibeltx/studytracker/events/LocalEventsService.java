package com.decibeltx.studytracker.events;

import com.decibeltx.studytracker.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class LocalEventsService implements EventsService {

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Override
  public void dispatchEvent(Activity activity) {
    StudyTrackerApplicationEvent event = new StudyTrackerApplicationEvent(this, activity);
    this.dispatchEvent(event);
  }

  @Override
  public void dispatchEvent(StudyTrackerEvent event) {
    eventPublisher.publishEvent(event);
  }
}
