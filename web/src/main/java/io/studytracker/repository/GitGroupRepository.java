package io.studytracker.repository;

import io.studytracker.model.GitGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitGroupRepository extends JpaRepository<GitGroup, Long> {

  @Query("select g from GitGroup g where g.organization.id = ?1")
  List<GitGroup> findByOrganizationId(Long organizationId);

}
