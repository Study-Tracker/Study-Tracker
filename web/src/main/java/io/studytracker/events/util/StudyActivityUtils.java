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
import io.studytracker.model.Comment;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.StudyConclusions;
import io.studytracker.model.StudyRelationship;
import io.studytracker.model.User;
import io.studytracker.storage.StorageFile;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudyActivityUtils {

  public static Activity fromNewStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.NEW_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    //    activity.setData(Collections.singletonMap("study",
    // EntityViewUtils.createStudyView(study)));
    activity.setData(Collections.singletonMap("study", EntityViewUtils.createStudyView(study)));
    return activity;
  }

  public static Activity fromUpdatedStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.UPDATED_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("study", EntityViewUtils.createStudyView(study)));
    return activity;
  }

  public static Activity fromDeletedStudy(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.DELETED_STUDY);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    activity.setData(Collections.singletonMap("study", EntityViewUtils.createStudyView(study)));
    return activity;
  }

  public static Activity fromStudyStatusChange(
      Study study, User triggeredBy, Status oldStatus, Status newStatus) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.STUDY_STATUS_CHANGED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("oldStatus", oldStatus.toString());
    data.put("newStatus", newStatus.toString());
    activity.setData(data);
    return activity;
  }

  public static Activity fromFileUpload(Study study, User triggeredBy, StorageFile storageFile) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.FILE_UPLOADED);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("file", EntityViewUtils.createStorageFileView(storageFile));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewConclusions(
      Study study, User triggeredBy, StudyConclusions conclusions) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.NEW_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("conclusions", EntityViewUtils.createStudyConclusionsView(conclusions));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedConclusions(
      Study study, User triggeredBy, StudyConclusions conclusions) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.EDITED_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("conclusions", EntityViewUtils.createStudyConclusionsView(conclusions));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedConclusions(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.DELETED_STUDY_CONCLUSIONS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewComment(Study study, User triggeredBy, Comment comment) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.NEW_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("comment", EntityViewUtils.createCommentView(comment));
    activity.setData(data);
    return activity;
  }

  public static Activity fromEditiedComment(Study study, User triggeredBy, Comment comment) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.EDITED_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("comment", EntityViewUtils.createCommentView(comment));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedComment(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.DELETED_COMMENT);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewStudyRelationship(
      Study sourceStudy, Study targetStudy, User triggeredBy, StudyRelationship relationship) {
    Activity activity = new Activity();
    activity.setStudy(sourceStudy);
    activity.setEventType(EventType.NEW_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudy", EntityViewUtils.createStudyView(targetStudy));
    data.put("sourceStudy", EntityViewUtils.createStudyView(sourceStudy));
    data.put("relationship", EntityViewUtils.createStudyRelationshipView(relationship));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedStudyRelationship(
      Study study, User triggeredBy, StudyRelationship relationship) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.UPDATED_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudy", EntityViewUtils.createStudyView(relationship.getTargetStudy()));
    data.put("sourceStudy", EntityViewUtils.createStudyView(study));
    data.put("relationship", EntityViewUtils.createStudyRelationshipView(relationship));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedStudyKeywords(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.UPDATED_STUDY_KEYWORDS);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedStudyRelationship(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.DELETED_STUDY_RELATIONSHIP);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    activity.setData(data);
    return activity;
  }

  public static Activity fromNewExternalLink(Study study, User triggeredBy, ExternalLink link) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.NEW_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("link", EntityViewUtils.createExternalLinkView(link));
    activity.setData(data);
    return activity;
  }

  public static Activity fromUpdatedExternalLink(Study study, User triggeredBy, ExternalLink link) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.UPDATED_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    data.put("link", EntityViewUtils.createExternalLinkView(link));
    activity.setData(data);
    return activity;
  }

  public static Activity fromDeletedExternalLink(Study study, User triggeredBy) {
    Activity activity = new Activity();
    activity.setStudy(study);
    activity.setEventType(EventType.DELETED_STUDY_EXTERNAL_LINK);
    activity.setDate(new Date());
    activity.setUser(triggeredBy);
    Map<String, Object> data = new HashMap<>();
    data.put("study", EntityViewUtils.createStudyView(study));
    activity.setData(data);
    return activity;
  }
}
