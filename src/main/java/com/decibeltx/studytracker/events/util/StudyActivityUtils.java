package com.decibeltx.studytracker.events.util;

import com.decibeltx.studytracker.events.dto.CommentView;
import com.decibeltx.studytracker.events.dto.ConclusionsView;
import com.decibeltx.studytracker.events.dto.StorageFileView;
import com.decibeltx.studytracker.events.dto.StudyRelationshipView;
import com.decibeltx.studytracker.events.dto.StudyView;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Activity.Reference;
import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.Conclusions;
import com.decibeltx.studytracker.model.EventType;
import com.decibeltx.studytracker.model.ExternalLink;
import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.StudyRelationship;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.storage.StorageFile;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudyActivityUtils {

  public static Activity fromNewStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.NEW_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("study", StudyView.from(study)));
    return activity;
  }

  public static Activity fromUpdatedStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.UPDATED_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("study", StudyView.from(study)));
    return activity;
  }

  public static Activity fromDeletedStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.DELETED_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("study", StudyView.from(study)));
    return activity;
  }

  public static Activity fromStudyStatusChange(Study study, User triggeredBy, Status oldStatus,
      Status newStatus) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.STUDY_STATUS_CHANGED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("oldStatus", oldStatus);
    data.put("newStatus", newStatus);
    activity.setData(data);
    return activity;
  }

  public static Activity fromFileUpload(Study study, User triggeredBy, StorageFile storageFile) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.FILE_UPLOADED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("file", StorageFileView.from(storageFile));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewConclusions(Study study, User triggeredBy,
      Conclusions conclusions) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.NEW_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("conclusions", ConclusionsView.from(conclusions));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedConclusions(Study study, User triggeredBy,
      Conclusions conclusions) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.EDITED_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("conclusions", ConclusionsView.from(conclusions));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedConclusions(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.DELETED_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewComment(Study study, User triggeredBy, Comment comment) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.NEW_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("comment", CommentView.from(comment));
    activity.setData(data);
    return activity;
  }

  public static Activity fromEditiedComment(Study study, User triggeredBy, Comment comment) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.EDITED_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("comment", CommentView.from(comment));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedComment(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.DELETED_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewStudyRelationship(Study sourceStudy, Study targetStudy,
      User triggeredBy, StudyRelationship relationship) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(sourceStudy.getId());
    activity.setEventType(EventType.NEW_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudy", StudyView.from(targetStudy));
    data.put("sourceStudy", StudyView.from(sourceStudy));
    data.put("relationship", StudyRelationshipView.from(relationship));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedStudyRelationship(Study study, User triggeredBy,
      StudyRelationship relationship) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.UPDATED_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudy", StudyView.from(relationship.getStudy()));
    data.put("sourceStudy", StudyView.from(study));
    data.put("relationship", relationship);
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedStudyRelationship(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.DELETED_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewExternalLink(Study study, User triggeredBy, ExternalLink link) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.NEW_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("link", link);
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedExternalLink(Study study, User triggeredBy, ExternalLink link) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.UPDATED_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    data.put("link", link);
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedExternalLink(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setReference(Reference.STUDY);
    activity.setReferenceId(study.getId());
    activity.setEventType(EventType.DELETED_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", StudyView.from(study));
    activity.setData(data);
    return activity;
  }

}
