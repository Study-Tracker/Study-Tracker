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

package io.studytracker.events;

import io.studytracker.config.HostInformation;
import io.studytracker.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class LocalEventsService implements EventsService {

  @Autowired private ApplicationEventPublisher eventPublisher;
  @Autowired private HostInformation hostInformation;

  @Override
  public void dispatchEvent(Activity activity) {
    StudyTrackerApplicationEvent event = new StudyTrackerApplicationEvent(this, activity, hostInformation);
    this.dispatchEvent(event);
  }

  @Override
  public void dispatchEvent(StudyTrackerEvent event) {
    eventPublisher.publishEvent(event);
  }
}
