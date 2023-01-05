/*
 * Copyright 2022 the original author or authors.
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

import io.studytracker.config.HostInformation;
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
  private final HostInformation host;

  public EventBridgeEvent(Activity activity, HostInformation host) {
    this.eventType = activity.getEventType();
    this.triggeredBy = activity.getUser().getEmail();
    this.date = activity.getDate();
    this.data = activity.getData();
    this.host = host;
  }
}
