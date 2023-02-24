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

package io.studytracker.service;

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Organization;
import io.studytracker.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

  private final OrganizationRepository organizationRepository;

  public OrganizationService(OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  public Organization getCurrentOrganization() throws RecordNotFoundException {
    LOGGER.debug("Finding current organization");
    return organizationRepository.findAll().stream().findFirst()
        .orElseThrow(() -> new RecordNotFoundException("Organization not found"));
  }

  @Transactional
  public Organization createOrganization(Organization organization) {
    LOGGER.info("Creating organization: {}", organization);
    return organizationRepository.save(organization);
  }

  @Transactional
  public Organization updateOrganization(Organization organization) {
    LOGGER.info("Updating organization: {}", organization);
    Organization o = organizationRepository.getById(organization.getId());
    o.setName(organization.getName());
    o.setActive(organization.isActive());
    o.setDescription(organization.getDescription());
    return organizationRepository.save(o);
  }

  @Transactional
  public void removeOrganization(Organization organization) {
    LOGGER.info("Removing organization: {}", organization);
    Organization o = organizationRepository.getById(organization.getId());
    o.setActive(false);
    organizationRepository.save(o);
  }

}
