package io.studytracker.repository;

import io.studytracker.model.GitLabGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitLabGroupRepository extends JpaRepository<GitLabGroup, Long> {

  @Query("select g from GitLabGroup g where g.gitLabIntegration.id = ?1")
  List<GitLabGroup> findByIntegrationId(Long integrationId);

  @Query("select g from GitLabGroup g where g.gitLabIntegration.organization.id = ?1")
  List<GitLabGroup> findByOrganizationId(Long organizationId);

  @Query("select g from GitLabGroup g where g.gitGroup.id = ?1")
  GitLabGroup findByGitGroupId(Long gitGroupId);

}
