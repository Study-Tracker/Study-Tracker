package io.studytracker.repository;

import io.studytracker.model.IntegrationInstanceConfigurationValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IntegrationInstanceConfigurationValueRepository
    extends JpaRepository<IntegrationInstanceConfigurationValue, Long> {

  @Query("select v from IntegrationInstanceConfigurationValue v where v.integrationInstance.id = ?1")
  List<IntegrationInstanceConfigurationValue> findByIntegrationInstanceId(Long integrationInstanceId);

}
