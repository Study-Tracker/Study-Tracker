package io.studytracker.service;

import io.studytracker.model.IntegrationInstance;
import io.studytracker.model.SupportedIntegration;
import io.studytracker.repository.IntegrationConfigurationSchemaFieldRepository;
import io.studytracker.repository.IntegrationInstanceConfigurationValueRepository;
import io.studytracker.repository.IntegrationInstanceRepository;
import io.studytracker.repository.SupportedIntegrationRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntegrationsService {

  private SupportedIntegrationRepository supportedIntegrationRepository;

  private IntegrationInstanceRepository integrationInstanceRepository;

  private IntegrationConfigurationSchemaFieldRepository schemaFieldRepository;

  private IntegrationInstanceConfigurationValueRepository configurationValueRepository;

  public static final Logger LOGGER = LoggerFactory.getLogger(IntegrationsService.class);

  /* Supported Integrations */

  public Optional<SupportedIntegration> findLatestSupportedIntegrationByName(String name) {
    return supportedIntegrationRepository.findLatestByName(name);
  }

  public List<SupportedIntegration> findSupportedIntegrationsByName(String name) {
    return supportedIntegrationRepository.findByName(name).stream()
        .sorted(Comparator.comparing(SupportedIntegration::getVersion))
        .collect(Collectors.toList());
  }

  public Optional<SupportedIntegration> findSupportedIntegrationByNameAndVersion(String name, Integer version) {
    return supportedIntegrationRepository.findByNameAndVersion(name, version);
  }

  @Transactional
  public SupportedIntegration registerSupportedIntegration(SupportedIntegration integration) {
    return supportedIntegrationRepository.save(integration);
  }


  /* Integration instances */

  /**
   * Returns a list of active integration instances for a given supported integration, defined by
   *   name. This list will include integrations for all parent versions, regardless of whether they
   *   are the latest definition or active.
   *
   * @param name
   * @return
   */
  public List<IntegrationInstance> findActiveIntegrationsInstanceByParentName(String name) {
    return integrationInstanceRepository.findBySupportedIntegrationName(name)
        .stream()
        .filter(IntegrationInstance::isActive)
        .collect(Collectors.toList());
  }

  /***
   * Registers a new {@link IntegrationInstance} with the system.
   *
   * @param instance the instance to register
   * @return the registered instance
   */
  @Transactional
  public IntegrationInstance createIntegrationInstance(IntegrationInstance instance) {
    return integrationInstanceRepository.save(instance);
  }

  /* Getters and setters */

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
