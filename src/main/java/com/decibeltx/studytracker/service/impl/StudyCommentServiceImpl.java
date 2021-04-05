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

package com.decibeltx.studytracker.service.impl;

import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.StudyRepository;
import com.decibeltx.studytracker.service.StudyCommentService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyCommentServiceImpl implements StudyCommentService {

  @Autowired
  private StudyRepository studyRepository;

  @Override
  public Optional<Comment> findStudyCommentById(Study study, String id) {
    List<Comment> comments = study.getComments().stream()
        .filter(c -> c.getId().equals(id))
        .collect(Collectors.toList());
    if (comments.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(comments.get(0));
    }
  }

  @Override
  public List<Comment> findStudyComments(Study study) {
    return study.getComments();
  }

  @Override
  public Comment addStudyComment(Study study, Comment comment) {
    comment.setId(UUID.randomUUID().toString());
    comment.setCreatedAt(new Date());
    study.getComments().add(comment);
    studyRepository.save(study);
    return comment;
  }

  @Override
  public Comment updateStudyComment(Study study, Comment comment) {
    comment.setUpdatedAt(new Date());
    List<Comment> comments = study.getComments().stream()
        .filter(s -> !s.getId().equals(comment.getId()))
        .collect(Collectors.toList());
    comments.add(comment);
    study.setComments(comments);
    studyRepository.save(study);
    return comment;
  }

  @Override
  public void deleteStudyComment(Study study, String commentId) {
    List<Comment> comments = study.getComments().stream()
        .filter(s -> !s.getId().equals(commentId))
        .collect(Collectors.toList());
    study.setComments(comments);
    studyRepository.save(study);
  }

}
