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

package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.Program;
import io.studytracker.model.User;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProgramActivityUtils {

  public static Activity fromNewProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setProgram(program);
    activity.setEventType(EventType.NEW_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", EntityViewUtils.createProgramView(program));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setProgram(program);
    activity.setEventType(EventType.UPDATED_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", EntityViewUtils.createProgramView(program));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedProgram(Program program, User triggeredBy) {
    Activity activity = new Activity();
    activity.setProgram(program);
    activity.setEventType(EventType.DELETED_PROGRAM);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("program", EntityViewUtils.createProgramView(program));
    activity.setData(data);
    return activity;
  }
}
