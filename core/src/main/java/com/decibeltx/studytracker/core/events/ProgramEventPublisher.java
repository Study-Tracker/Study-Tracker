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

import com.decibeltx.studytracker.core.events.ProgramEvent.Type;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ProgramEventPublisher {

  @Autowired
  private ApplicationEventPublisher publisher;

  public void publishProgramEvent(Program program, User user, Type type, Object data) {
    publisher.publishEvent(new ProgramEvent(this, program, user, type, data));
  }

  public void publishProgramEvent(Program program, User user, Type type) {
    this.publishProgramEvent(program, user, type, null);
  }

}
