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

package com.decibeltx.studytracker.web.controller.api;

import com.decibeltx.studytracker.core.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.core.exception.RecordNotFoundException;
import com.decibeltx.studytracker.core.model.Activity;
import com.decibeltx.studytracker.core.model.Comment;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.StudyCommentService;
import com.decibeltx.studytracker.web.controller.UserAuthenticationUtils;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/study/{studyId}/comments")
@RestController
public class StudyCommentsController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCommentsController.class);

  @Autowired
  private StudyCommentService studyCommentService;

  @GetMapping("")
  public List<Comment> getStudyComments(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return study.getComments();
  }

  @PostMapping("")
  public HttpEntity<Comment> addStudyComment(@PathVariable("studyId") String studyId,
      @RequestBody Comment comment) {

    LOGGER
        .info(String.format("Creating new comment for study %s: %s", studyId, comment.toString()));

    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    study.setLastModifiedBy(user);
    studyCommentService.addStudyComment(study, comment);

    // Publish events
    Activity activity = StudyActivityUtils.fromNewComment(study, user, comment);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(comment, HttpStatus.CREATED);
  }

  @PutMapping("/{commentId}")
  public HttpEntity<Comment> editedStudyComment(@PathVariable("studyId") String studyId,
      @PathVariable("commentId") String commentId, @RequestBody Comment updated) {

    LOGGER.info(String.format("Editing comment for study %s: %s", studyId, updated.toString()));

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Study study = getStudyFromIdentifier(studyId);
    Optional<Comment> optional = studyCommentService.findStudyCommentById(study, commentId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException(String.format("No comment with ID %s found for study %s",
          commentId, study.getCode()));
    }
    Comment comment = optional.get();

    comment.setText(updated.getText());
    study.setLastModifiedBy(user);

    studyCommentService.updateStudyComment(study, comment);

    // Publish events
    Activity activity = StudyActivityUtils.fromEditiedComment(study, user, comment);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(comment, HttpStatus.OK);
  }

  @DeleteMapping("/{commentId}")
  public HttpEntity<?> deleteStudyComment(@PathVariable("studyId") String studyId,
      @PathVariable("commentId") String commentId) {

    LOGGER.info(String.format("Removing comment %s for study %s", commentId, studyId));

    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    study.setLastModifiedBy(user);
    Optional<Comment> optional = studyCommentService.findStudyCommentById(study, commentId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException(String.format("No comment with ID %s found for study %s",
          commentId, study.getCode()));
    }

    studyCommentService.deleteStudyComment(study, commentId);

    // Publish events
    Activity activity = StudyActivityUtils.fromDeletedComment(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
