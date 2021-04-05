package com.decibeltx.studytracker.events;

import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.service.EventsService;
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
