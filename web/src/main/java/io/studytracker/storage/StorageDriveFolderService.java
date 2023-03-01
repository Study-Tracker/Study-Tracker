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

import io.studytracker.model.Assay;
import io.studytracker.model.Organization;
import io.studytracker.model.Program;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.model.StorageDriveFolder;
import io.studytracker.model.StorageDriveFolderDetails;
import io.studytracker.model.Study;
import io.studytracker.repository.StorageDriveFolderDetailsOperations;
import io.studytracker.repository.StorageDriveFolderRepository;
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
  @Autowired private StorageDriveFolderRepository folderRepository;
  @Autowired private StudyFileStorageServiceLookup studyFileStorageServiceLookup;
  @Autowired private StorageDriveRepositoryLookup storageDriveRepositoryLookup;


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

  public List<StorageDriveFolder> findByProgram(Program program) {
    return folderRepository.findByProgramId(program.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryProgramFolder(Program program) {
    return folderRepository.findPrimaryByProgramId(program.getId());
  }

  public List<StorageDriveFolder> findByStudy(Study study) {
    return folderRepository.findByStudyId(study.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryStudyFolder(Study study) {
    return folderRepository.findPrimaryByStudyId(study.getId());
  }

  public List<StorageDriveFolder> findByAssay(Assay assay) {
    return folderRepository.findByAssayId(assay.getId());
  }

  public Optional<StorageDriveFolder> findPrimaryAssayFolder(Assay assay) {
    return folderRepository.findPrimaryByAssayId(assay.getId());
  }

  public StudyStorageService lookupStudyStorageService(StorageDriveFolder folder) {
    return studyFileStorageServiceLookup.lookup(folder.getStorageDrive().getDriveType())
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for folder: "
        + folder.getId()));
  }

  public StudyStorageService lookupStudyStorageService(DriveType driveType) {
    return studyFileStorageServiceLookup.lookup(driveType)
        .orElseThrow(() -> new IllegalArgumentException("No storage service found for drive type: "
            + driveType));
  }

  public StorageDriveFolderDetails lookupFolderDetails(StorageDriveFolder folder) {
     StorageDriveFolderDetailsOperations<?> repository = storageDriveRepositoryLookup
         .lookupFolderRepository(folder.getStorageDrive().getDriveType());
     return ((Optional<StorageDriveFolderDetails>) repository.findByStorageDriveFolderId(folder.getId()))
         .orElseThrow(() -> new IllegalArgumentException("No folder details found for folder: " + folder.getId()));
  }



}
