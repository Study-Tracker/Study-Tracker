package com.decibeltx.studytracker.events.util;

import com.decibeltx.studytracker.events.EventType;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.model.User;
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
