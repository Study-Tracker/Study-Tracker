/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.aws;

import io.studytracker.integration.IntegrationService;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.repository.AwsIntegrationRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AwsIntegrationService implements IntegrationService<AwsIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AwsIntegrationService.class);

  private final AwsIntegrationRepository awsIntegrationRepository;

  public AwsIntegrationService(AwsIntegrationRepository awsIntegrationRepository) {
    this.awsIntegrationRepository = awsIntegrationRepository;
  }

  @Override
  public Optional<AwsIntegration> findById(Long id) {
    return awsIntegrationRepository.findById(id);
  }

  @Override
  public List<AwsIntegration> findByOrganization(Organization organization) {
    LOGGER.debug("Finding AWS integrations for organization: {}", organization);
    return awsIntegrationRepository.findByOrganizationId(organization.getId());
  }

  @Transactional
  @Override
  public AwsIntegration register(AwsIntegration awsIntegration) {
    LOGGER.info("Creating AWS integration: {}", awsIntegration);
    return awsIntegrationRepository.save(awsIntegration);
  }

  @Transactional
  @Override
  public AwsIntegration update(AwsIntegration awsIntegration) {
    LOGGER.info("Updating AWS integration: {}", awsIntegration);
    AwsIntegration i = awsIntegrationRepository.getById(awsIntegration.getId());
    i.setName(awsIntegration.getName());
    i.setAccountNumber(awsIntegration.getAccountNumber());
    i.setActive(awsIntegration.isActive());
    i.setAccessKeyId(awsIntegration.getAccessKeyId());
    i.setSecretAccessKey(awsIntegration.getSecretAccessKey());
    i.setUseIam(awsIntegration.isUseIam());
    return awsIntegrationRepository.save(i);
  }

  @Transactional
  @Override
  public void remove(AwsIntegration integration) {
    LOGGER.info("Removing AWS integration: {}", integration.getId());
    AwsIntegration i = awsIntegrationRepository.getById(integration.getId());
    i.setAccountNumber(null);
    i.setActive(false);
    i.setAccessKeyId(null);
    i.setSecretAccessKey(null);
    i.setUseIam(false);
    awsIntegrationRepository.save(i);
  }

  @Override
  public boolean validate(AwsIntegration instance) {
    return false;
  }

  @Override
  public boolean test(AwsIntegration instance) {
    return false;
  }
}
