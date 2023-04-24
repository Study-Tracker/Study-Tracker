package io.studytracker.repository;

import io.studytracker.model.GitRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {

  @Query("select r from GitRepository r where r.gitGroup.id = ?1")
  List<GitRepository> findByGitGroupId(Long gitGroupId);

}
