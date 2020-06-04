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

package com.decibeltx.studytracker.web.listeners;

import com.decibeltx.studytracker.core.events.type.EventType;
import com.decibeltx.studytracker.core.events.type.StudyEvent;
import com.decibeltx.studytracker.core.model.ExternalLink;
import com.decibeltx.studytracker.core.model.Message;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.DocumentService;
import com.decibeltx.studytracker.core.service.MessagingService;
import com.decibeltx.studytracker.core.service.StudyExternalLinkService;
import com.decibeltx.studytracker.teams.TeamsMessageUtils;
import com.decibeltx.studytracker.teams.TeamsMessagingService;
import com.decibeltx.studytracker.teams.entity.DriveItem;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class NewStudyMessagingListener implements ApplicationListener<StudyEvent> {

  @Autowired(required = false)
  private MessagingService messagingService;

  @Autowired(required = false)
  private DocumentService documentService;

  @Autowired
  private StudyExternalLinkService externalLinkService;

  @Override
  public void onApplicationEvent(StudyEvent studyEvent) {
    if (messagingService != null && studyEvent.getEventType().equals(EventType.NEW_STUDY)) {
      Study study = studyEvent.getStudy();
      Resource resource = null;
      String content = null;
      if (documentService != null && messagingService instanceof TeamsMessagingService) {
        TeamsMessagingService teamsMessagingService = (TeamsMessagingService) messagingService;
        File file = documentService.createStudySummarySlideShow(study);
        resource = new FileSystemResource(file);
        DriveItem driveItem = teamsMessagingService.uploadStudyFile(study, resource);
        ExternalLink fileLink = new ExternalLink();
        fileLink.setLabel(DocumentService.SUMMARY_DOCUMENT_LINK_LABEL);
        fileLink.setUrl(driveItem.getWebUrl());
        externalLinkService.addStudyExternalLink(study, fileLink);
        content = TeamsMessageUtils.newStudyMessage(study, driveItem.getWebUrl());
      } else {
        content = TeamsMessageUtils.newStudyMessage(study);
      }
      Message message = messagingService.sendStudyMessage(content, study);
      ExternalLink messageLink = new ExternalLink();
      messageLink.setUrl(message.getUrl());
      messageLink.setLabel(TeamsMessageUtils.NEW_STUDY_LINK_LABEL);
      externalLinkService.addStudyExternalLink(study, messageLink);
    }
  }
}
