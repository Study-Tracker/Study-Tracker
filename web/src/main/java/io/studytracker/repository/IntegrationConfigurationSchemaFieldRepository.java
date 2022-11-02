package io.studytracker.repository;

import io.studytracker.model.IntegrationConfigurationSchemaField;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IntegrationConfigurationSchemaFieldRepository
    extends JpaRepository<IntegrationConfigurationSchemaField, Long> {

  @Query("select f from IntegrationConfigurationSchemaField f where f.supportedIntegration.id = ?1")
  List<IntegrationConfigurationSchemaField> findBySupportedIntegrationId(Long supportedIntegrationId);

}
