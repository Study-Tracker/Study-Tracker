package io.studytracker.events;

import io.studytracker.model.Activity;

/**
 * Simple service for dispatching events to external message brokers. Events are defined as {@link
 * Activity} instances, with optionally attached metadata.
 */
public interface EventsService {

  void dispatchEvent(Activity activity);

  void dispatchEvent(StudyTrackerEvent event);
}
