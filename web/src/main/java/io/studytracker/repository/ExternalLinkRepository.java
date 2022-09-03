package io.studytracker.repository;

import io.studytracker.model.ExternalLink;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExternalLinkRepository extends JpaRepository<ExternalLink, Long> {

  @EntityGraph("link-only")
  @Query("select l from ExternalLink l where l.study.id = ?1")
  List<ExternalLink> findByStudyId(Long studyId);

  @Query("select l from ExternalLink l where l.study.id = ?1")
  Page<ExternalLink> findByStudyId(Long studyId, Pageable pageable);

}
