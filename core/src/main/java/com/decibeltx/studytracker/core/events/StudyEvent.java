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

import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

public class StudyEvent extends ApplicationEvent {

  public enum Type {
    NEW_STUDY,
    UPDATED_STUDY,
    DELETED_STUDY,
    STUDY_STATUS_CHANGED,
    FILE_UPLOADED,
    NEW_STUDY_CONCLUSIONS,
    EDITED_STUDY_CONCLUSIONS,
    DELETED_STUDY_CONCLUSIONS,
    NEW_COMMENT,
    EDITED_COMMENT,
    DELETED_COMMENT,
    NEW_STUDY_RELATIONSHIP,
    UPDATED_STUDY_RELATIONSHIP,
    DELETED_STUDY_RELATIONSHIP,
    NEW_STUDY_EXTERNAL_LINK,
    UPDATED_STUDY_EXTERNAL_LINK,
    DELETED_STUDY_EXTERNAL_LINK,
    NEW_ASSAY,
    UPDATED_ASSAY,
    DELETED_ASSAY,
    ASSAY_STATUS_CHANGED
  }

  private final Type type;
  private final Study study;
  private final User user;
  private final Object data;

  public StudyEvent(@NonNull Object source, @NonNull Study study, @NonNull User user,
      @NonNull Type type, Object data) {
    super(source);
    this.type = type;
    this.study = study;
    this.user = user;
    this.data = data;
  }

  public Type getType() {
    return type;
  }

  public Study getStudy() {
    return study;
  }

  public User getUser() {
    return user;
  }

  public Object getData() {
    return data;
  }

  @Override
  public String toString() {
    return "StudyEvent{" +
        "type=" + type +
        ", study=" + study +
        ", user=" + user +
        ", data=" + data +
        '}';
  }
}
