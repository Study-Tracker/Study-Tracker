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

package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.NotebookEntryTemplate;
import io.studytracker.model.User;
import java.util.Collections;
import java.util.Date;

public class EntryTemplateActivityUtils {

  private static Activity createActivity(
      NotebookEntryTemplate notebookEntryTemplate, User triggeredBy, EventType eventType) {
    Activity activity = new Activity();
    activity.setEventType(eventType);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("entryTemplate", notebookEntryTemplate));
    return activity;
  }

  public static Activity fromNewEntryTemplate(
      NotebookEntryTemplate notebookEntryTemplate, User triggeredBy) {
    return createActivity(notebookEntryTemplate, triggeredBy, EventType.NEW_ENTRY_TEMPLATE);
  }

  public static Activity fromUpdatedEntryTemplate(
      NotebookEntryTemplate notebookEntryTemplate, User triggeredBy) {
    return createActivity(notebookEntryTemplate, triggeredBy, EventType.UPDATED_ENTRY_TEMPLATE);
  }
}
