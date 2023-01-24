/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.config.HostInformation;
import io.studytracker.events.EventsService;
import io.studytracker.events.StudyTrackerEvent;
import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest;
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse;
import software.amazon.awssdk.services.eventbridge.model.PutEventsResultEntry;

public class EventBridgeService implements EventsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventBridgeService.class);

  @Autowired
  private HostInformation hostInformation;

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
    EventBridgeEvent event = new EventBridgeEvent(activity, hostInformation);
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
    PutEventsRequestEntry entry =
        PutEventsRequestEntry.builder()
            .eventBusName(eventBusName)
            .source("study-tracker")
            .detailType(event.getEventType().toString())
            .detail(json)
            .build();
    PutEventsRequest request = PutEventsRequest.builder().entries(entry).build();
    PutEventsResponse response = client.putEvents(request);
    for (PutEventsResultEntry resultEntry : response.entries()) {
      System.out.println(resultEntry.toString());
    }
  }
}
