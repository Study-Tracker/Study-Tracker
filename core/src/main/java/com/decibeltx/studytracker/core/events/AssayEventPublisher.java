/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.core.events;

import com.decibeltx.studytracker.core.events.type.AssayEvent;
import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AssayEventPublisher {

  @Autowired
  private ApplicationEventPublisher publisher;

  public void publishNewAssayEvent(Assay assay, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", assay.getName());
    data.put("code", assay.getCode());
    publisher.publishEvent(new AssayEvent(this, assay, triggeredBy, EventType.NEW_ASSAY, data));
  }

  public void publishUpdatedAssayEvent(Assay assay, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", assay.getName());
    data.put("code", assay.getCode());
    publisher.publishEvent(new AssayEvent(this, assay, triggeredBy, EventType.UPDATED_ASSAY, data));
  }

  public void publishDeletedAssayEvent(Assay assay, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", assay.getName());
    data.put("code", assay.getCode());
    publisher.publishEvent(new AssayEvent(this, assay, triggeredBy, EventType.DELETED_ASSAY, data));
  }

  public void publishAssayStatusChangedEvent(Assay assay, User triggeredBy, Status oldStatus,
      Status newStatus) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", assay.getCode());
    data.put("name", assay.getName());
    data.put("oldStatus", oldStatus);
    data.put("newStatus", newStatus);
    publisher.publishEvent(
        new AssayEvent(this, assay, triggeredBy, EventType.ASSAY_STATUS_CHANGED, data));
  }

}
