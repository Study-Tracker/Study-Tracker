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

package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.model.Comment;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.repository.CommentRepository;
import com.decibeltx.studytracker.repository.StudyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyCommentService {

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private StudyRepository studyRepository;

  public Optional<Comment> findStudyCommentById(Long id) {
    return commentRepository.findById(id);
  }

  public List<Comment> findStudyComments(Study study) {
    return commentRepository.findByStudyId(study.getId());
  }

  @Transactional
  public Comment addStudyComment(Study study, Comment comment) {
    comment.setStudy(study);
    commentRepository.save(comment);
    return comment;
  }

  @Transactional
  public Comment updateStudyComment(Comment comment) {
    Comment c = commentRepository.getById(comment.getId());
    c.setText(comment.getText());
    commentRepository.save(c);
    return c;
  }

  @Transactional
  public void deleteStudyComment(Study study, Comment comment) {
    study.removeComment(comment);
    studyRepository.save(study);
  }

}
