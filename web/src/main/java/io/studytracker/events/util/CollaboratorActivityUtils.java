package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.Collaborator;
import io.studytracker.model.User;
import java.util.Collections;
import java.util.Date;

public class CollaboratorActivityUtils {

  public static Activity fromNewCollaborator(Collaborator collaborator, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_COLLABORATOR);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collaborator", EntityViewUtils.createCollaboratorView(collaborator)));
    return activity;
  }

  public static Activity fromUpdatedCollaborator(Collaborator collaborator, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.UPDATED_COLLABORATOR);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collaborator", EntityViewUtils.createCollaboratorView(collaborator)));
    return activity;
  }

  public static Activity fromDeletedCollaborator(Collaborator collaborator, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.DELETED_COLLABORATOR);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collaborator", EntityViewUtils.createCollaboratorView(collaborator)));
    return activity;
  }

}
