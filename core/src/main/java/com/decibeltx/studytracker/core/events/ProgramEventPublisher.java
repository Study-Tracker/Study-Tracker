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

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.events.type.ProgramEvent;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProgramEventPublisher {

  @Autowired
  private ApplicationEventPublisher publisher;

  public void publishNewProgramEvent(Program program, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", program.getName());
    data.put("code", program.getCode());
    publisher
        .publishEvent(new ProgramEvent(this, program, triggeredBy, EventType.NEW_PROGRAM, data));
  }

  public void publishUpdatedProgramEvent(Program program, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", program.getName());
    data.put("code", program.getCode());
    publisher.publishEvent(
        new ProgramEvent(this, program, triggeredBy, EventType.UPDATED_PROGRAM, data));
  }

  public void publishDeletedProgramEvent(Program program, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("name", program.getName());
    data.put("code", program.getCode());
    publisher.publishEvent(
        new ProgramEvent(this, program, triggeredBy, EventType.DELETED_PROGRAM, data));
  }

}
