/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.events;

public enum EventType {

  // For testing only
  TEST_EVENT,

  // Study events
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
  UPDATED_STUDY_KEYWORDS,

  // Assay Events
  NEW_ASSAY,
  UPDATED_ASSAY,
  DELETED_ASSAY,
  ASSAY_STATUS_CHANGED,

  // Assay Tasks
  ASSAY_TASK_ADDED,
  ASSAY_TASK_UPDATED,
  ASSAY_TASK_DELETED,

  // Assay Type Events
  NEW_ASSAY_TYPE,
  UPDATED_ASSAY_TYPE,
  DELETED_ASSAY_TYPE,

  // Program Events
  NEW_PROGRAM,
  UPDATED_PROGRAM,
  DELETED_PROGRAM,

  // Users
  NEW_USER,
  UPDATED_USER,
  DELETED_USER,

  // EntryTemplate events
  NEW_ENTRY_TEMPLATE,
  UPDATED_ENTRY_TEMPLATE,

  // Study Collections
  NEW_STUDY_COLLECTION,
  UPDATED_STUDY_COLLECTION,
  DELETED_STUDY_COLLECTION,
  STUDY_ADDED_TO_COLLECTION,
  STUDY_REMOVED_FROM_COLLECTION,

  // Collaborators
  NEW_COLLABORATOR,
  UPDATED_COLLABORATOR,
  DELETED_COLLABORATOR,

  // Storage Locations
  NEW_STORAGE_LOCATION,
  UPDATED_STORAGE_LOCATION,
  DELETED_STORAGE_LOCATION,

  // Integration Instance
  NEW_INTEGRATION_INSTANCE,
  UPDATED_INTEGRATION_INSTANCE,
  DELETED_INTEGRATION_INSTANCE,
}
