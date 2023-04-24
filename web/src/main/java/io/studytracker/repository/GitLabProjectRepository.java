package io.studytracker.repository;

import io.studytracker.model.GitLabProject;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitLabProjectRepository extends JpaRepository<GitLabProject, Long> {

  @Query("select r from GitLabProject r where r.gitLabGroup.id = ?1")
  List<GitLabProject> findByGroupId(Long groupId);

  @Query("select p from GitLabProject p where p.gitRepository.id = ?1")
  GitLabProject findByRepositoryId(Long repositoryId);

}
