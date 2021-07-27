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

package com.decibeltx.studytracker.controller.api;

import com.decibeltx.studytracker.controller.UserAuthenticationUtils;
import com.decibeltx.studytracker.events.util.StudyActivityUtils;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.mapstruct.dto.CommentDto;
import com.decibeltx.studytracker.mapstruct.mapper.CommentMapper;
import com.decibeltx.studytracker.model.Activity;
import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.service.StudyCommentService;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
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

  @Autowired
  private CommentMapper commentMapper;

  @GetMapping("")
  public List<CommentDto> getStudyComments(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return commentMapper.toDtoList(studyCommentService.findStudyComments(study));
  }

  @PostMapping("")
  public HttpEntity<CommentDto> addStudyComment(@PathVariable("studyId") String studyId,
      @RequestBody @Valid CommentDto dto) {

    LOGGER
        .info(String.format("Creating new comment for study %s: %s", studyId, dto.toString()));

    Comment comment = commentMapper.fromDto(dto);

    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    studyCommentService.addStudyComment(study, comment);

    // Publish events
    Activity activity = StudyActivityUtils.fromNewComment(study, user, comment);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(commentMapper.toDto(comment), HttpStatus.CREATED);
  }

  @PutMapping("/{commentId}")
  public HttpEntity<CommentDto> editedStudyComment(@PathVariable("studyId") String studyId,
      @PathVariable("commentId") Long commentId, @RequestBody @Valid CommentDto dto) {

    LOGGER.info(String.format("Editing comment for study %s: %s", studyId, dto.toString()));

    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Study study = getStudyFromIdentifier(studyId);
    Comment comment = commentMapper.fromDto(dto);

    studyCommentService.updateStudyComment(comment);
    this.getStudyService().markAsUpdated(study, user);

    // Publish events
    Activity activity = StudyActivityUtils.fromEditiedComment(study, user, comment);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(commentMapper.toDto(comment), HttpStatus.OK);
  }

  @DeleteMapping("/{commentId}")
  public HttpEntity<?> deleteStudyComment(@PathVariable("studyId") String studyId,
      @PathVariable("commentId") Long commentId) {

    LOGGER.info(String.format("Removing comment %s for study %s", commentId, studyId));

    Study study = getStudyFromIdentifier(studyId);
    String username = UserAuthenticationUtils
        .getUsernameFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
    User user = getUserService().findByUsername(username)
        .orElseThrow(RecordNotFoundException::new);

    Optional<Comment> optional = studyCommentService.findStudyCommentById(commentId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException(String.format("No comment with ID %s found for study %s",
          commentId, study.getCode()));
    }

    studyCommentService.deleteStudyComment(study, optional.get());

    // Publish events
    Activity activity = StudyActivityUtils.fromDeletedComment(study, user);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
