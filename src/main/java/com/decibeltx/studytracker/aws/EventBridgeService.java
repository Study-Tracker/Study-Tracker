package com.decibeltx.studytracker.aws;

import com.decibeltx.studytracker.events.EventsService;
import com.decibeltx.studytracker.events.StudyTrackerEvent;
import com.decibeltx.studytracker.exception.StudyTrackerException;
import com.decibeltx.studytracker.model.Activity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

public class EventBridgeService implements EventsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventBridgeService.class);

  private final EventBridgeClient client;

  private final ObjectMapper objectMapper;

  private final String eventBusName;

  public EventBridgeService(EventBridgeClient client, String eventBusName) {
    this.client = client;
    this.objectMapper = new ObjectMapper();
    this.eventBusName = eventBusName;
  }

  @Override
  public void dispatchEvent(Activity activity) {
    EventBridgeEvent event = new EventBridgeEvent(activity);
    this.dispatchEvent(event);
  }

  @Override
  public void dispatchEvent(StudyTrackerEvent event) {
    String json;
    try {
      json = objectMapper.writeValueAsString(event);
    } catch (JsonProcessingException e) {
      throw new StudyTrackerException(e);
    }
    LOGGER.info("Dispatching event with data: " + json);
    PutEventsRequestEntry entry = PutEventsRequestEntry.builder()
        .eventBusName(eventBusName)
        .source("study-tracker")
        .detailType(event.getEventType().toString())
        .detail(json)
        .build();
    PutEventsRequest request = PutEventsRequest.builder()
        .entries(entry)
        .build();
    PutEventsResponse response = client.putEvents(request);
    for (PutEventsResultEntry resultEntry : response.entries()) {
      System.out.println(resultEntry.toString());
    }
  }
}
