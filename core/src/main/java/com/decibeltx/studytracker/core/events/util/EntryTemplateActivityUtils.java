package com.decibeltx.studytracker.core.events.util;

import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Activity.Reference;
import com.decibeltx.studytracker.core.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.core.model.EventType;
import com.decibeltx.studytracker.core.model.User;

import java.util.Collections;
import java.util.Date;

public class EntryTemplateActivityUtils {

    private static Activity createActivity(NotebookEntryTemplate notebookEntryTemplate, User triggeredBy, EventType eventType) {
        Activity activity = new Activity();
        activity.setReference(Reference.ENTRY_TEMPLATE);
        activity.setReferenceId(notebookEntryTemplate.getId());
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
