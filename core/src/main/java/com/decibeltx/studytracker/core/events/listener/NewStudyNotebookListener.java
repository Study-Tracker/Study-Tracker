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
import com.decibeltx.studytracker.core.notebook.NotebookEntry;
import com.decibeltx.studytracker.core.notebook.NotebookService;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NewStudyNotebookListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(NewStudyNotebookListener.class);

  @Autowired(required = false)
  private NotebookService notebookService;

  @Autowired
  private StudyRepository studyRepository;

  @EventListener
  public void onApplicationEvent(StudyEvent studyEvent) {
    if (studyEvent.getEventType().equals(EventType.NEW_STUDY)) {
      Study study = studyEvent.getStudy();
      LOGGER.warn(String.format("TODO: Creating ELN entry for study: %s", study.getCode()));

      if (notebookService != null) {

        if (study.isLegacy()) {
          LOGGER.warn(String.format("Legacy Study : %s", study.getCode()));
          NotebookEntry notebookEntry = study.getNotebookEntry();
          notebookEntry.setLabel("Benchling");
          study.setNotebookEntry(notebookEntry);
          study.setUpdatedAt(new Date());
          studyRepository.save(study);
        } else {
          try {
            NotebookEntry notebookEntry = notebookService.createStudyEntry(study);
            study.setNotebookEntry(notebookEntry);
            study.setUpdatedAt(new Date());
            studyRepository.save(study);
          } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Failed to create notebook entry for study: " + study.getCode());
          }
        }
      } else {
        LOGGER.warn(String.format("No notebook mode selected: "));

      }

    } else {
      LOGGER.warn(String.format("Not New Study"));
    }
  }
}

