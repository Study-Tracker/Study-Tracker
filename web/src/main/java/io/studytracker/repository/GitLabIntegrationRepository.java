package io.studytracker.repository;

import io.studytracker.model.GitLabIntegration;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitLabIntegrationRepository extends JpaRepository<GitLabIntegration, Long> {

  @Query("select g from GitLabIntegration g where g.organization.id = ?1")
  List<GitLabIntegration> findByOrganizationId(Long organizationId);

}
