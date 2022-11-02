package io.studytracker.service;

import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.SupportedIntegration;
import io.studytracker.repository.IntegrationConfigurationSchemaFieldRepository;
import io.studytracker.repository.IntegrationInstanceConfigurationValueRepository;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.repository.SupportedIntegrationRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IntegrationsService {

  private SupportedIntegrationRepository supportedIntegrationRepository;

  private IntegrationInstanceRepository integrationInstanceRepository;

  private IntegrationConfigurationSchemaFieldRepository schemaFieldRepository;

  private IntegrationInstanceConfigurationValueRepository configurationValueRepository;

  public static final Logger LOGGER = LoggerFactory.getLogger(IntegrationsService.class);

  /**
   * Lists all supported integrations.
   *
   * @return a list of supported integrations
   */
  public List<SupportedIntegration> findSupportedIntegrations() {
    return supportedIntegrationRepository.findAll();
  }


  /**
   * Lists all active integration instances.
   * @return a list of active integration instances
   */
  public List<SupportedIntegration> findActiveSupportedIntegrations() {
    return supportedIntegrationRepository.findByActive(true);
  }

  public Optional<SupportedIntegration> findLatestSupportedIntegrationByName(String name) {
    return supportedIntegrationRepository.findLatestByName(name);
  }

  /**
   * Lists all configured integrations instances.
   *
   * @return a list of configured integration instances
   */
  public List<IntegrationInstance> findIntegrationInstances() {
    return integrationInstanceRepository.findAll();
  }

  /**
   * Returns a list of all active integration instances.
   *
   * @return a list of active integration instances
   */
  public List<IntegrationInstance> findActiveIntegrationInstances() {
    return integrationInstanceRepository.findByActive(true);
  }

  @Autowired
  public void setSupportedIntegrationRepository(
      SupportedIntegrationRepository supportedIntegrationRepository) {
    this.supportedIntegrationRepository = supportedIntegrationRepository;
  }

  @Autowired
  public void setIntegrationInstanceRepository(
      IntegrationInstanceRepository integrationInstanceRepository) {
    this.integrationInstanceRepository = integrationInstanceRepository;
  }

  @Autowired
  public void setSchemaFieldRepository(
      IntegrationConfigurationSchemaFieldRepository schemaFieldRepository) {
    this.schemaFieldRepository = schemaFieldRepository;
  }

  @Autowired
  public void setConfigurationValueRepository(
      IntegrationInstanceConfigurationValueRepository configurationValueRepository) {
    this.configurationValueRepository = configurationValueRepository;
  }
}
