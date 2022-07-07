package io.studytracker.events.util;

import io.studytracker.events.EventType;
import io.studytracker.model.Activity;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.model.User;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudyCollectionActivityUtils {

  public static Activity fromNewStudyCollection(StudyCollection collection, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_STUDY_COLLECTION);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collection", EntityViewUtils.createStudyCollectionView(collection)));
    return activity;
  }

  public static Activity fromUpdatedStudyCollection(StudyCollection collection, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.UPDATED_STUDY_COLLECTION);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collection", EntityViewUtils.createStudyCollectionView(collection)));
    return activity;
  }

  public static Activity fromDeletedStudyCollection(StudyCollection collection, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.DELETED_STUDY_COLLECTION);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setData(
        Collections.singletonMap(
            "collection", EntityViewUtils.createStudyCollectionView(collection)));
    return activity;
  }

  public static Activity fromStudyAddedToCollection(
      Study study, StudyCollection collection, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_STUDY_COLLECTION);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setStudy(study);
    Map<String, Object> data = new HashMap<>();
    data.put("collection", EntityViewUtils.createStudyCollectionView(collection));
    data.put("study", EntityViewUtils.createStudyView(study));
    return activity;
  }

  public static Activity fromStudyRemovedFromCollection(
      Study study, StudyCollection collection, User user) {
    Activity activity = new Activity();
    activity.setEventType(EventType.NEW_STUDY_COLLECTION);
    activity.setDate(new Date());
    activity.setUser(user);
    activity.setStudy(study);
    Map<String, Object> data = new HashMap<>();
    data.put("collection", EntityViewUtils.createStudyCollectionView(collection));
    data.put("study", EntityViewUtils.createStudyView(study));
    return activity;
  }
}
