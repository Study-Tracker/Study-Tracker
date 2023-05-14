package io.studytracker.repository;

import io.studytracker.model.GitLabRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitLabRepositoryRepository extends JpaRepository<GitLabRepository, Long> {

  @Query("select r from GitLabRepository r where r.gitLabGroup.id = ?1")
  List<GitLabRepository> findByGroupId(Long groupId);

  @Query("select p from GitLabRepository p where p.gitRepository.id = ?1")
  GitLabRepository findByRepositoryId(Long repositoryId);

}
