package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.NotebookEntryTemplate;
import io.studytracker.model.User;
import java.util.Collections;
import java.util.Date;

public class EntryTemplateActivityUtils {

    private static Activity createActivity(NotebookEntryTemplate notebookEntryTemplate, User triggeredBy, EventType eventType) {
        Activity activity = new Activity();
        activity.setEventType(eventType);
        activity.setDate(new Date());
        activity.setUser(triggeredBy);
        activity.setData(Collections.singletonMap("entryTemplate", notebookEntryTemplate));
        return activity;
    }

    public static Activity fromNewEntryTemplate(NotebookEntryTemplate notebookEntryTemplate, User triggeredBy) {
        return createActivity(notebookEntryTemplate, triggeredBy, EventType.NEW_ENTRY_TEMPLATE);
    }

    public static Activity fromUpdatedEntryTemplate(NotebookEntryTemplate notebookEntryTemplate, User triggeredBy) {
        return createActivity(notebookEntryTemplate, triggeredBy, EventType.UPDATED_ENTRY_TEMPLATE);
    }
}
