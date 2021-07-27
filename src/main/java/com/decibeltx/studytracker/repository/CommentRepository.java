package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Query("select c from Comment c where c.study.id = ?1")
  List<Comment> findByStudyId(Long id);

}
