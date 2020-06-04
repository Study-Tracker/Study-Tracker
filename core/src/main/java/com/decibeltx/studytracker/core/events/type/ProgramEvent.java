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

package com.decibeltx.studytracker.core.events.type;

import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import java.util.Map;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class ProgramEvent extends ApplicationEvent implements StudyTrackerEvent {

  private final EventType eventType;

  private final Program program;

  private final User user;

  private final Map<String, Object> data;

  public ProgramEvent(@NonNull Object source, @NonNull Program program, @NonNull User user,
      @NonNull EventType eventType, @NonNull Map<String, Object> data) {
    super(source);
    this.eventType = eventType;
    this.program = program;
    this.user = user;
    this.data = data;
  }

  @Override
  public EventType getEventType() {
    return eventType;
  }

  public Program getProgram() {
    return program;
  }

  public User getUser() {
    return user;
  }

  @Override
  public Map<String, Object> getData() {
    return data;
  }

  @Override
  public String toString() {
    return "ProgramEvent{" +
        "eventType=" + eventType +
        ", program=" + program.getName() +
        ", user=" + user.getAccountName() +
        ", data=" + data +
        '}';
  }

}
