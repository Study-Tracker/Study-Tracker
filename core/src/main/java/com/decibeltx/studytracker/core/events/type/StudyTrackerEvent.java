package com.decibeltx.studytracker.core.events.type;

import java.util.Map;

/**
 * Simple base class for capturing life-cycle events of Study Tracker data.
 */
public interface StudyTrackerEvent {

  /**
   * Returns the {@link EventType} of the event, which dictates the life-cycle event and the data
   * that will be captured.
   *
   * @return event type
   */
  EventType getEventType();

  /**
   * Returns the optional data associated withthe event.
   *
   * @return event data
   */
  Map<String, Object> getData();

}
