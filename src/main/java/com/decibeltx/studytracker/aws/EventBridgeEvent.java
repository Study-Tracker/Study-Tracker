package com.decibeltx.studytracker.aws;

import com.decibeltx.studytracker.events.EventType;
import com.decibeltx.studytracker.events.StudyTrackerEvent;
import com.decibeltx.studytracker.model.Activity;
import java.util.Date;
import java.util.Map;
import lombok.Getter;

@Getter
public class EventBridgeEvent implements StudyTrackerEvent {

  private final EventType eventType;
  private final String triggeredBy;
  private final Date date;
  private final Map<String, Object> data;

  public EventBridgeEvent(Activity activity) {
    this.eventType = activity.getEventType();
    this.triggeredBy = activity.getUser().getUsername();
    this.date = activity.getDate();
    this.data = activity.getData();
  }

}
