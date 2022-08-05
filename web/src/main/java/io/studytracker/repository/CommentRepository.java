package io.studytracker.repository;

import io.studytracker.model.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  Page<Comment> findByStudyId(Long studyId, Pageable pageable);

  @Query("select c from Comment c where c.study.id = ?1")
  List<Comment> findByStudyId(Long id);
}
