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

package io.studytracker.egnyte;

import io.studytracker.egnyte.entity.EgnyteObject;
import io.studytracker.egnyte.rest.EgnyteRestApiClient;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.EgnyteDriveDetails;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class EgnyteIntegrationService implements IntegrationService<EgnyteIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteIntegrationService.class);

  private final EgnyteIntegrationRepository egnyteIntegrationRepository;
  private final StorageDriveRepository storageDriveRepository;

  public EgnyteIntegrationService(EgnyteIntegrationRepository egnyteIntegrationRepository,
      StorageDriveRepository storageDriveRepository) {
    this.egnyteIntegrationRepository = egnyteIntegrationRepository;
    this.storageDriveRepository = storageDriveRepository;
  }


  @Override
  public Optional<EgnyteIntegration> findById(Long id) {
    return egnyteIntegrationRepository.findById(id);
  }

  @Override
  public List<EgnyteIntegration> findByOrganization(Organization organization) {
    LOGGER.debug("Finding Egnyte integrations for organization: {}", organization.getName());
    return egnyteIntegrationRepository.findByOrganizationId(organization.getId());
  }

  @Override
  @Transactional
  public EgnyteIntegration register(EgnyteIntegration egnyteIntegration) {
    LOGGER.info("Creating Egnyte integration: {}", egnyteIntegration);
    if (!validate(egnyteIntegration)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(egnyteIntegration)) {
      throw new IllegalArgumentException("Failed to connect to Egnyte with the provided credentials.");
    }
    egnyteIntegration.setActive(true);
    return egnyteIntegrationRepository.save(egnyteIntegration);
  }

  @Override
  @Transactional
  public EgnyteIntegration update(EgnyteIntegration egnyteIntegration) {
    LOGGER.info("Updating Egnyte integration: {}", egnyteIntegration);
    if (!validate(egnyteIntegration)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(egnyteIntegration)) {
      throw new IllegalArgumentException("Failed to connect to Egnyte with the provided credentials.");
    }
    EgnyteIntegration i = egnyteIntegrationRepository.getById(egnyteIntegration.getId());
    i.setTenantName(egnyteIntegration.getTenantName());
    i.setRootUrl(egnyteIntegration.getRootUrl());
    i.setActive(egnyteIntegration.isActive());
    i.setApiToken(egnyteIntegration.getApiToken());
    i.setQps(egnyteIntegration.getQps());
    return egnyteIntegrationRepository.save(i);
  }

  @Override
  public boolean validate(EgnyteIntegration instance) {
    return StringUtils.hasText(instance.getTenantName())
        && StringUtils.hasText(instance.getApiToken())
        && StringUtils.hasText(instance.getRootUrl());
  }

  @Override
  public boolean test(EgnyteIntegration instance) {
    EgnyteRestApiClient client = EgnyteClientFactory.createRestApiClient(instance);
    try {
      EgnyteObject object = client.findObjectByPath("/Shared");
      return object != null && object.isFolder();
    } catch (Exception e) {
      LOGGER.error("Egnyte integration test failed", e);
      return false;
    }
  }

  @Override
  @Transactional
  public void remove(EgnyteIntegration integration) {
    LOGGER.info("Removing Egnyte integration: {}", integration.getId());
    EgnyteIntegration i = egnyteIntegrationRepository.getById(integration.getId());
    i.setActive(false);
    egnyteIntegrationRepository.save(i);
  }

  // Egnyte drives

  public List<StorageDrive> listIntegrationDrives(EgnyteIntegration egnyteIntegration) {
    Organization organization = egnyteIntegration.getOrganization();
    return storageDriveRepository.findByOrganizationAndDriveType(organization.getId(), DriveType.EGNYTE)
        .stream()
        .filter(drive -> drive.getDetails() instanceof EgnyteDriveDetails
            && ((EgnyteDriveDetails) drive.getDetails()).getEgnyteIntegrationId().equals(egnyteIntegration.getId()))
        .collect(Collectors.toList());
  }

  public Optional<StorageDrive> findDriveById(Long driveId) {
    Optional<StorageDrive> optional = storageDriveRepository.findById(driveId);
    if (optional.isPresent()) {
      StorageDrive drive = optional.get();
      if (drive.getDetails() instanceof EgnyteDriveDetails) {
        return optional;
      } else {
        throw new IllegalArgumentException("Drive is not an Egnyte drive");
      }
    } else {
      return Optional.empty();
    }
  }

  @Transactional
  public StorageDrive registerDefaultDrive(EgnyteIntegration egnyteIntegration) {

    StorageDrive storageDrive = new StorageDrive();
    storageDrive.setOrganization(egnyteIntegration.getOrganization());
    storageDrive.setDriveType(DriveType.EGNYTE);
    storageDrive.setDisplayName("Egnyte Shared Drive");
    storageDrive.setActive(true);
    storageDrive.setRootPath("/Shared");

    EgnyteDriveDetails details = new EgnyteDriveDetails();
    details.setName("Shared");
    details.setEgnyteIntegrationId(egnyteIntegration.getId());
    storageDrive.setDetails(details);

    return storageDriveRepository.save(storageDrive);

  }

  @Transactional
  public StorageDrive updateDrive(StorageDrive storageDrive) {
    StorageDrive d = storageDriveRepository.getById(storageDrive.getId());
    d.setActive(storageDrive.isActive());
    d.setDisplayName(storageDrive.getDisplayName());
    d.setRootPath(storageDrive.getRootPath());
    d.setDetails(storageDrive.getDetails());
    return storageDriveRepository.save(d);
  }

  @Transactional
  public void updateDriveStatus(StorageDrive drive, boolean active) {
    StorageDrive d = storageDriveRepository.getById(drive.getId());
    d.setActive(active);
    storageDriveRepository.save(d);
  }

  @Transactional
  public void removeDrive(StorageDrive drive) {
    StorageDrive s = storageDriveRepository.getById(drive.getId());
    s.setActive(false);
    storageDriveRepository.save(s);
  }

}
