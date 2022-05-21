package io.studytracker.aws;

import io.studytracker.events.EventType;
import io.studytracker.events.StudyTrackerEvent;
import io.studytracker.model.Activity;
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
