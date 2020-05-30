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

import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.User;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class ProgramEvent extends ApplicationEvent {

  private final Type type;
  private final Program program;
  private final User user;
  private final Object data;

  public ProgramEvent(@NonNull Object source, @NonNull Program program, @NonNull User user,
      @NonNull Type type, Object data) {
    super(source);
    this.type = type;
    this.program = program;
    this.user = user;
    this.data = data;
  }

  public Type getType() {
    return type;
  }

  public Program getProgram() {
    return program;
  }

  public User getUser() {
    return user;
  }

  public Object getData() {
    return data;
  }

  @Override
  public String toString() {
    return "StudyEvent{" +
        "type=" + type +
        ", program=" + program +
        ", user=" + user +
        ", data=" + data +
        '}';
  }

  public enum Type {
    NEW_PROGRAM,
    UPDATED_PROGRAM,
    DELETED_PROGRAM
  }
}
