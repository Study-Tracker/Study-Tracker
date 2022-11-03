package io.studytracker.repository;

import io.studytracker.model.IntegrationInstance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IntegrationInstanceRepository extends JpaRepository<IntegrationInstance, Long> {

  List<IntegrationInstance> findByActive(Boolean active);

  Optional<IntegrationInstance> findByName(String name);

  Optional<IntegrationInstance> findByDisplayName(String displayName);

  @Query("select i from IntegrationInstance i where i.supportedIntegration.id = ?1")
  List<IntegrationInstance> findBySupportedIntegrationId(Long supportedIntegrationId);

  @Query("select i from IntegrationInstance  i where i.supportedIntegration.name = ?1")
  List<IntegrationInstance> findBySupportedIntegrationName(String name);

}
