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

package com.decibeltx.studytracker.core.events.listener;

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.events.type.StudyEvent;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.storage.StorageFolder;
import com.decibeltx.studytracker.core.storage.StudyStorageService;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NewStudyFolderListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewStudyFolderListener.class);

  @Autowired
  private StudyStorageService studyStorageService;

  @Autowired
  private StudyRepository studyRepository;

  @EventListener
  public void onApplicationEvent(StudyEvent studyEvent) {
    if (studyEvent.getEventType().equals(EventType.NEW_STUDY)) {
      Study study = studyEvent.getStudy();
      LOGGER.info(String.format("Creating storage folder for study: %s", study.getCode()));
      try {
        studyStorageService.createStudyFolder(study);
        StorageFolder folder = studyStorageService.getStudyFolder(study);
        study.setStorageFolder(folder);
        study.setUpdatedAt(new Date());
        studyRepository.save(study);
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.error("Failed to create storage folder for study: " + study.getCode());
      }
    }
  }
}
