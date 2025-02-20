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

package io.studytracker.controller.api.v1;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.CommentDto;
import io.studytracker.mapstruct.dto.api.CommentPayloadDto;
import io.studytracker.mapstruct.mapper.CommentMapper;
import io.studytracker.model.Comment;
import io.studytracker.model.Study;
import io.studytracker.repository.CommentRepository;
import io.studytracker.service.StudyCommentService;
import io.studytracker.service.StudyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentPublicController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommentPublicController.class);

  @Autowired
  private StudyCommentService studyCommentService;

  @Autowired
  private StudyService studyService;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentMapper commentMapper;

  @GetMapping("")
  public Page<CommentDto> getStudyComments(
      @RequestParam(required = false) Long studyId,
      Pageable pageable
  ) {
    LOGGER.debug("Fetching comments");
    Page<Comment> page;
    if (studyId != null) {
      page = commentRepository.findByStudyId(studyId, pageable);
    } else {
      page = commentRepository.findAll(pageable);
    }
    return new PageImpl<>(commentMapper.toDtoList(page.getContent()), pageable, page.getTotalElements());
  }

  @GetMapping("/{id}")
  public CommentDto getCommentById(@PathVariable Long id) {
    LOGGER.debug("Fetching comment with id: " + id);
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find comment with id: " + id));
    return commentMapper.toDto(comment);
  }

  @PostMapping("")
  public HttpEntity<CommentDto> createComment(@RequestBody CommentPayloadDto dto) {
    LOGGER.info("Creating comment: {}", dto);
    Study study = studyService.findById(dto.getStudyId())
        .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + dto.getStudyId()));
    Comment comment = studyCommentService.addStudyComment(study, commentMapper.fromPayloadDto(dto));
    return new ResponseEntity<>(commentMapper.toDto(comment), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<CommentDto> updateComment(@PathVariable("id") Long id, @RequestBody CommentPayloadDto dto) {
    LOGGER.info("Updating comment with id: {}", id);
    Comment comment = studyCommentService.updateStudyComment(commentMapper.fromPayloadDto(dto));
    return new ResponseEntity<>(commentMapper.toDto(comment), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteComment(@PathVariable("id") Long id) {
    LOGGER.info("Deleting comment with id: {}", id);
    Comment comment = commentRepository.findById(id)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find comment with id: " + id));
    Study study = studyService.findById(comment.getStudy().getId())
            .orElseThrow(() -> new RecordNotFoundException("Cannot find study with id: " + comment.getStudy().getId()));
    studyCommentService.deleteStudyComment(study, comment);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
