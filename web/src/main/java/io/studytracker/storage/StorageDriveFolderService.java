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

package io.studytracker.storage;

import io.studytracker.model.Organization;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.repository.StorageDriveFolderRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageDriveFolderService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageDriveFolderService.class);

  @Autowired private OrganizationService organizationService;
  @Autowired private StorageDriveRepository driveRepository;
  @Autowired private StorageDriveFolderRepository folderRepository;

  public List<StorageDriveFolder> findAll() {
    LOGGER.debug("Find all drive folders for organization");
    Organization organization = organizationService.getCurrentOrganization();
    return folderRepository.findByOrganization(organization.getId());
  }

  public List<StorageDriveFolder> findStudyRootFolders() {
    LOGGER.debug("Find all study root drive folders for organization");
    Organization organization = organizationService.getCurrentOrganization();
    return folderRepository.findStudyRootByOrganization(organization.getId());
  }

  public List<StorageDriveFolder> findBrowserRootFolders() {
    LOGGER.debug("Find all browser root drive folders for organization");
    Organization organization = organizationService.getCurrentOrganization();
    return folderRepository.findBrowserRootByOrganization(organization.getId());
  }

  public Optional<StorageDriveFolder> findById(Long id) {
    LOGGER.debug("Find drive folder by id: {}", id);
    Organization organization = organizationService.getCurrentOrganization();
    return folderRepository.findByIdAndOrganization(id, organization.getId());
  }

}
