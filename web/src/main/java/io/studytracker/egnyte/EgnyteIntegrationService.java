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

import io.studytracker.integration.IntegrationService;
import io.studytracker.model.EgnyteDrive;
import io.studytracker.model.EgnyteDriveFolder;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.EgnyteDriveFolderRepository;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EgnyteIntegrationService implements IntegrationService<EgnyteIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EgnyteIntegrationService.class);

  private final EgnyteIntegrationRepository egnyteIntegrationRepository;
  private final EgnyteDriveRepository egnyteDriveRepository;
  private final StorageDriveRepository storageDriveRepository;
  private final EgnyteDriveFolderRepository egnyteDriveFolderRepository;

  public EgnyteIntegrationService(EgnyteIntegrationRepository egnyteIntegrationRepository,
      EgnyteDriveRepository egnyteDriveRepository, StorageDriveRepository storageDriveRepository,
      EgnyteDriveFolderRepository egnyteDriveFolderRepository) {
    this.egnyteIntegrationRepository = egnyteIntegrationRepository;
    this.egnyteDriveRepository = egnyteDriveRepository;
    this.storageDriveRepository = storageDriveRepository;
    this.egnyteDriveFolderRepository = egnyteDriveFolderRepository;
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
    return egnyteIntegrationRepository.save(egnyteIntegration);
  }

  @Override
  @Transactional
  public EgnyteIntegration update(EgnyteIntegration egnyteIntegration) {
    LOGGER.info("Updating Egnyte integration: {}", egnyteIntegration);
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
    return false;
  }

  @Override
  public boolean test(EgnyteIntegration instance) {
    return false;
  }

  @Override
  @Transactional
  public void remove(EgnyteIntegration integration) {
    LOGGER.info("Removing Egnyte integration: {}", integration.getId());
    EgnyteIntegration i = egnyteIntegrationRepository.getById(integration.getId());
    i.setActive(false);
    egnyteIntegrationRepository.save(i);
  }

  public EgnyteIntegration findByStorageDrive(StorageDrive storageDrive) {
    return egnyteIntegrationRepository.findByStorageDriveId(storageDrive.getId());
  }

  // Egnyte drives

  public List<EgnyteDrive> listIntegrationDrives(EgnyteIntegration egnyteIntegration) {
    return egnyteDriveRepository.findByIntegrationId(egnyteIntegration.getId());
  }

  public EgnyteDrive registerDefaultDrive(EgnyteIntegration egnyteIntegration) {

    StorageDrive storageDrive = new StorageDrive();
    storageDrive.setOrganization(egnyteIntegration.getOrganization());
    storageDrive.setDriveType(DriveType.EGNYTE);
    storageDrive.setDisplayName("Egnyte Shared Drive");
    storageDrive.setActive(true);
    storageDrive.setRootPath("/Shared");

    EgnyteDrive drive = new EgnyteDrive();
    drive.setEgnyteIntegration(egnyteIntegration);
    drive.setStorageDrive(storageDrive);
    drive.setName("Shared");

    return egnyteDriveRepository.save(drive);

  }

  public EgnyteDrive updateDrive(EgnyteDrive drive) {

    StorageDrive storageDrive = drive.getStorageDrive();
    StorageDrive d = storageDriveRepository.getById(storageDrive.getId());
    d.setActive(storageDrive.isActive());
    d.setDisplayName(storageDrive.getDisplayName());
    d.setRootPath(storageDrive.getRootPath());

    EgnyteDrive e = egnyteDriveRepository.getById(drive.getId());
    e.setStorageDrive(d);
    e.setName(drive.getName());

    return egnyteDriveRepository.save(e);

  }

  public void removeDrive(Long id) {
    EgnyteDrive e = egnyteDriveRepository.getById(id);
    StorageDrive s = e.getStorageDrive();
    s.setActive(false);
    e.setStorageDrive(s);
    egnyteDriveRepository.save(e);
  }

  // Folders

  public EgnyteDriveFolder registerFolder(EgnyteDriveFolder folder) {
    return egnyteDriveFolderRepository.save(folder);
  }

}
