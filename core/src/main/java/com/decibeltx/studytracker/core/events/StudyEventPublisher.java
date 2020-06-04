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

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.events.type.StudyEvent;
import com.decibeltx.studytracker.core.model.Comment;
import com.decibeltx.studytracker.core.model.Conclusions;
import com.decibeltx.studytracker.core.model.ExternalLink;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.StudyRelationship;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.storage.StorageFile;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class StudyEventPublisher {

  @Autowired
  private ApplicationEventPublisher publisher;

  public void publishStudyEvent(Study study, User user, EventType eventType,
      Map<String, Object> data) {
    publisher.publishEvent(new StudyEvent(this, study, user, eventType, data));
  }

  public void publishStudyEvent(Study study, User user, EventType eventType) {
    this.publishStudyEvent(study, user, eventType, null);
  }

  public void publishNewStudyEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("program", study.getProgram().getName());
    data.put("code", study.getCode());
    data.put("externalCode", study.getExternalCode());
    data.put("createdAt", study.getCreatedAt());
    data.put("createdBy", study.getCreatedBy().getAccountName());
    data.put("description", study.getDescription());
    data.put("name", study.getName());
    data.put("status", study.getStatus());
    data.put("keywords", study.getKeywords());
    StudyEvent event = new StudyEvent(this, study, triggeredBy, EventType.NEW_STUDY, data);
    publisher.publishEvent(event);
  }

  public void publishUpdatedStudyEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("program", study.getProgram().getName());
    data.put("code", study.getCode());
    data.put("externalCode", study.getExternalCode());
    data.put("createdAt", study.getCreatedAt());
    data.put("createdBy", study.getCreatedBy().getAccountName());
    data.put("updatedAt", study.getUpdatedAt());
    data.put("lastModifiedBy", study.getLastModifiedBy().getAccountName());
    data.put("description", study.getDescription());
    data.put("name", study.getName());
    data.put("status", study.getStatus());
    data.put("keywords", study.getKeywords());
    StudyEvent event = new StudyEvent(this, study, triggeredBy, EventType.UPDATED_STUDY, data);
    publisher.publishEvent(event);
  }

  public void publishDeletedStudyEvent(Study study, User triggeredBy) {
    this.publishStudyEvent(study, triggeredBy, EventType.DELETED_STUDY);
  }

  public void publishStudyStatusChangedEvent(Study study, User triggeredBy, Status oldStatus,
      Status newStatus) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("oldStatus", oldStatus);
    data.put("newStatus", newStatus);
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.STUDY_STATUS_CHANGED, data));
  }

  public void publishFileUploadEvent(Study study, User triggeredBy, StorageFile storageFile) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("fileName", storageFile.getName());
    data.put("filePath", storageFile.getPath());
    data.put("url", storageFile.getUrl());
    publisher.publishEvent(new StudyEvent(this, study, triggeredBy, EventType.FILE_UPLOADED, data));
  }

  public void publishNewConclusionsEvent(Study study, User triggeredBy, Conclusions conclusions) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("content", conclusions.getContent());
    data.put("createdBy", conclusions.getCreatedBy().getAccountName());
    data.put("createdAt", conclusions.getCreatedAt());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.NEW_STUDY_CONCLUSIONS, data));
  }

  public void publishUpdatedConclusionsEvent(Study study, User triggeredBy,
      Conclusions conclusions) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("content", conclusions.getContent());
    data.put("createdBy", conclusions.getCreatedBy().getAccountName());
    data.put("createdAt", conclusions.getCreatedAt());
    data.put("lastModifiedBy", conclusions.getLastModifiedBy().getAccountName());
    data.put("updatedAt", conclusions.getUpdatedAt());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.EDITED_STUDY_CONCLUSIONS, data));
  }

  public void publishDeletedConclusionsEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.DELETED_STUDY_CONCLUSIONS, data));
  }

  public void publishNewCommentEvent(Study study, User triggeredBy, Comment comment) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("text", comment.getText());
    data.put("createdBy", comment.getCreatedBy().getAccountName());
    data.put("createdAt", comment.getCreatedAt());
    publisher.publishEvent(new StudyEvent(this, study, triggeredBy, EventType.NEW_COMMENT, data));
  }

  public void publishEditedCommentEvent(Study study, User triggeredBy, Comment comment) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("text", comment.getText());
    data.put("createdBy", comment.getCreatedBy().getAccountName());
    data.put("createdAt", comment.getCreatedAt());
    data.put("updatedAt", comment.getUpdatedAt());
    publisher
        .publishEvent(new StudyEvent(this, study, triggeredBy, EventType.EDITED_COMMENT, data));
  }

  public void publishDeletedCommentEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    publisher
        .publishEvent(new StudyEvent(this, study, triggeredBy, EventType.DELETED_COMMENT, data));
  }

  public void publishNewRelationshipEvent(Study study, User triggeredBy,
      StudyRelationship relationship) {
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudyCode", relationship.getStudy().getCode());
    data.put("targetStudyName", relationship.getStudy().getName());
    data.put("sourceStudyCode", study.getCode());
    data.put("sourceStudyName", study.getName());
    data.put("type", relationship.getType());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.NEW_STUDY_RELATIONSHIP, data));
  }

  public void publishUpdatedRelationshipEvent(Study study, User triggeredBy,
      StudyRelationship relationship) {
    Map<String, Object> data = new HashMap<>();
    data.put("targetStudyCode", relationship.getStudy().getCode());
    data.put("targetStudyName", relationship.getStudy().getName());
    data.put("sourceStudyCode", study.getCode());
    data.put("sourceStudyName", study.getName());
    data.put("type", relationship.getType());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.UPDATED_STUDY_RELATIONSHIP, data));
  }

  public void publishDeletedRelationshipEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.DELETED_STUDY_RELATIONSHIP, data));
  }

  public void publishNewExternalLinkEvent(Study study, User triggeredBy, ExternalLink link) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("label", link.getLabel());
    data.put("url", link.getUrl());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.NEW_STUDY_EXTERNAL_LINK, data));
  }

  public void publishUpdatedExternalLinkEvent(Study study, User triggeredBy, ExternalLink link) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    data.put("label", link.getLabel());
    data.put("url", link.getUrl());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.UPDATED_STUDY_EXTERNAL_LINK, data));
  }

  public void publishDeletedExternalLinkEvent(Study study, User triggeredBy) {
    Map<String, Object> data = new HashMap<>();
    data.put("code", study.getCode());
    data.put("name", study.getName());
    publisher.publishEvent(
        new StudyEvent(this, study, triggeredBy, EventType.DELETED_STUDY_EXTERNAL_LINK, data));
  }

}
