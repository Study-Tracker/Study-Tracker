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

package io.studytracker.controller.api;

import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.ExternalLinkDto;
import io.studytracker.mapstruct.mapper.ExternalLinkMapper;
import io.studytracker.model.Activity;
import io.studytracker.model.ExternalLink;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.service.StudyExternalLinkService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/study/{id}/links")
@RestController
public class StudyExternalLinksController extends AbstractStudyController {

  @Autowired private StudyExternalLinkService studyExternalLinkService;

  @Autowired private ExternalLinkMapper externalLinkMapper;

  @GetMapping("")
  public List<ExternalLinkDto> getStudyExternalLinks(@PathVariable("id") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return externalLinkMapper.toDtoList(studyExternalLinkService.findAllStudyExternalLinks(study));
  }

  @PostMapping("")
  public HttpEntity<ExternalLinkDto> addExternalLink(
      @PathVariable("id") String studyId,
      @RequestBody @Valid ExternalLinkDto dto
  ) {
    Study study = getStudyFromIdentifier(studyId);
    ExternalLink externalLink = externalLinkMapper.fromDto(dto);
    studyExternalLinkService.addStudyExternalLink(study, externalLink);

    // Publish events
    Activity activity =
        StudyActivityUtils.fromNewExternalLink(study, this.getAuthenticatedUser(), externalLink);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(externalLinkMapper.toDto(externalLink), HttpStatus.CREATED);
  }

  @PutMapping("/{linkId}")
  public HttpEntity<ExternalLinkDto> editExternalLink(
      @PathVariable("id") String studyId,
      @PathVariable("linkId") Long linkId,
      @RequestBody @Valid ExternalLinkDto dto
  ) {
    Study study = getStudyFromIdentifier(studyId);
    User user = this.getAuthenticatedUser();
    Optional<ExternalLink> optional = studyExternalLinkService.findById(linkId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException("Cannot find external link with ID: " + linkId);
    }
    ExternalLink externalLink = externalLinkMapper.fromDto(dto);
    studyExternalLinkService.updateStudyExternalLink(study, externalLink);
    this.getStudyService().markAsUpdated(study, user);

    // Publish events
    Activity activity = StudyActivityUtils.fromUpdatedExternalLink(study, user, externalLink);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(externalLinkMapper.toDto(externalLink), HttpStatus.OK);
  }

  @DeleteMapping("/{linkId}")
  public HttpEntity<?> removeExternalLink(
      @PathVariable("id") String studyId,
      @PathVariable("linkId") Long linkId
  ) {
    Study study = getStudyFromIdentifier(studyId);
    User user = this.getAuthenticatedUser();
    study.setLastModifiedBy(user);
    studyExternalLinkService.deleteStudyExternalLink(study, linkId);

    // Publish events
    Activity activity = StudyActivityUtils.fromDeletedExternalLink(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
