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

package io.studytracker.repository;

import io.studytracker.model.StorageDriveFolder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StorageDriveFolderRepository extends JpaRepository<StorageDriveFolder, Long> {

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.storageDrive.id = ?1")
  List<StorageDriveFolder> findByStorageDriveId(Long storageDriveId);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.storageDrive.organization.id = ?1")
  List<StorageDriveFolder> findByOrganization(Long organizationId);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.storageDrive.organization.id = ?1 AND f.studyRoot = true")
  List<StorageDriveFolder> findStudyRootByOrganization(Long organizationId);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.storageDrive.organization.id = ?1 AND f.browserRoot = true")
  List<StorageDriveFolder> findBrowserRootByOrganization(Long organizationId);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.id = ?1 AND f.storageDrive.organization.id = ?2")
  Optional<StorageDriveFolder> findByIdAndOrganization(Long id, Long organizationId);

}
