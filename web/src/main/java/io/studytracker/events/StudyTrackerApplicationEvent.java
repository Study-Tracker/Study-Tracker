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

package io.studytracker.events;

import io.studytracker.config.HostInformation;
import io.studytracker.model.Activity;
import java.util.Date;
import java.util.Map;
import org.springframework.context.ApplicationEvent;

public class StudyTrackerApplicationEvent extends ApplicationEvent implements StudyTrackerEvent {

  private final Activity activity;
  private final HostInformation host;

  public StudyTrackerApplicationEvent(Object source, Activity activity, HostInformation host) {
    super(source);
    this.activity = activity;
    this.host = host;
  }

  public Activity getActivity() {
    return activity;
  }

  @Override
  public EventType getEventType() {
    return activity.getEventType();
  }

  @Override
  public Map<String, Object> getData() {
    return activity.getData();
  }

  @Override
  public String getTriggeredBy() {
    return activity.getUser().getUsername();
  }

  @Override
  public Date getDate() {
    return activity.getDate();
  }

  @Override
  public HostInformation getHost() {
    return host;
  }
}
