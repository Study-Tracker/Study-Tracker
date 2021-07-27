package com.decibeltx.studytracker.events;

import java.util.Date;
import java.util.Map;

public interface StudyTrackerEvent {
  EventType getEventType();
  Map<String, Object> getData();
  String getTriggeredBy();
  Date getDate();
}
