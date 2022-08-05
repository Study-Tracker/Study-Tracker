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

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.events.util.StudyActivityUtils;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.CommentFormDto;
import io.studytracker.mapstruct.dto.response.CommentDetailsDto;
import io.studytracker.model.Activity;
import io.studytracker.model.Comment;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RequestMapping("/api/internal/study/{studyId}/comments")
@RestController
public class StudyCommentsController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyCommentsController.class);

  @GetMapping("")
  public List<CommentDetailsDto> getStudyComments(@PathVariable("studyId") String studyId) {
    Study study = getStudyFromIdentifier(studyId);
    return this.getCommentMapper().toDetailsDtoList(this.getStudyCommentService().findStudyComments(study));
  }

  @PostMapping("")
  public HttpEntity<CommentDetailsDto> addStudyComment(
      @PathVariable("studyId") String studyId,
      @RequestBody @Valid CommentFormDto dto
  ) {

    LOGGER.info(String.format("Creating new comment for study %s: %s", studyId, dto.toString()));

    Comment comment = this.getCommentMapper().fromFormDto(dto);

    Study study = getStudyFromIdentifier(studyId);
    this.getStudyCommentService().addStudyComment(study, comment);

    // Publish events
    Activity activity = StudyActivityUtils.fromNewComment(study, this.getAuthenticatedUser(), comment);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(this.getCommentMapper().toDetailsDto(comment), HttpStatus.CREATED);
  }

  @PutMapping("/{commentId}")
  public HttpEntity<CommentDetailsDto> editedStudyComment(
      @PathVariable("studyId") String studyId,
      @PathVariable("commentId") Long commentId,
      @RequestBody @Valid CommentFormDto dto
  ) {

    LOGGER.info(String.format("Editing comment for study %s: %s", studyId, dto.toString()));

    User user = this.getAuthenticatedUser();
    Study study = getStudyFromIdentifier(studyId);
    Comment comment = this.getCommentMapper().fromFormDto(dto);
    comment.setId(commentId);

    Comment updated = this.getStudyCommentService().updateStudyComment(comment);
    this.getStudyService().markAsUpdated(study, user);

    // Publish events
    Activity activity = StudyActivityUtils.fromEditiedComment(study, user, updated);
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(this.getCommentMapper().toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{commentId}")
  public HttpEntity<?> deleteStudyComment(
      @PathVariable("studyId") String studyId,
      @PathVariable("commentId") Long commentId
  ) {

    LOGGER.info(String.format("Removing comment %s for study %s", commentId, studyId));

    Study study = getStudyFromIdentifier(studyId);

    Optional<Comment> optional = this.getStudyCommentService().findStudyCommentById(commentId);
    if (!optional.isPresent()) {
      throw new RecordNotFoundException(
          String.format("No comment with ID %s found for study %s", commentId, study.getCode()));
    }

    this.getStudyCommentService().deleteStudyComment(study, optional.get());

    // Publish events
    Activity activity = StudyActivityUtils.fromDeletedComment(study, this.getAuthenticatedUser());
    getActivityService().create(activity);
    getEventsService().dispatchEvent(activity);

    return new ResponseEntity<>(HttpStatus.OK);
  }
}
