package com.decibeltx.studytracker.core.events;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.service.EventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class LocalEventsService implements EventsService {

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Override
  public void dispatchEvent(Activity activity) {
    eventPublisher.publishEvent(new StudyTrackerEvent(this, activity));
  }

}
