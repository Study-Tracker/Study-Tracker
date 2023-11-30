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
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StorageDriveFolderRepository extends JpaRepository<StorageDriveFolder, Long> {
  
  @Override
  @EntityGraph("storage-drive-folder-details")
  Optional<StorageDriveFolder> findById(Long id);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.storageDrive.id = ?1")
  @EntityGraph("storage-drive-folder-details")
  List<StorageDriveFolder> findByStorageDriveId(Long storageDriveId);

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.studyRoot = true")
  @EntityGraph("storage-drive-folder-details")
  List<StorageDriveFolder> findStudyRoot();

  @Query("SELECT f FROM StorageDriveFolder f WHERE f.browserRoot = true")
  @EntityGraph("storage-drive-folder-details")
  List<StorageDriveFolder> findBrowserRoot();

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN ProgramStorageFolder pf ON f.id = pf.storageDriveFolder.id "
      + " JOIN Program p ON p.id = pf.program.id"
      + " WHERE p.id = ?1")
  List<StorageDriveFolder> findByProgramId(Long programId);

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN ProgramStorageFolder pf ON f.id = pf.storageDriveFolder.id "
      + " JOIN Program p ON p.id = pf.program.id"
      + " WHERE pf.primary = true and p.id = ?1")
  Optional<StorageDriveFolder> findPrimaryByProgramId(Long programId);

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN StudyStorageFolder sf ON sf.storageDriveFolder.id = f.id "
      + " JOIN Study s ON s.id = sf.study.id "
      + " WHERE s.id = ?1")
  List<StorageDriveFolder> findByStudyId(Long studyId);

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN StudyStorageFolder sf ON sf.storageDriveFolder.id = f.id "
      + " JOIN Study s ON s.id = sf.study.id "
      + " WHERE sf.primary = true AND s.id = ?1")
  Optional<StorageDriveFolder> findPrimaryByStudyId(Long studyId);

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN AssayStorageFolder af ON af.storageDriveFolder.id = f.id "
      + " JOIN Assay a ON a.id = af.assay.id "
      + " WHERE a.id = ?1")
  List<StorageDriveFolder> findByAssayId(Long assayId);

  @EntityGraph("storage-drive-folder-details")
  @Query("SELECT f FROM StorageDriveFolder f "
      + " JOIN AssayStorageFolder af ON af.storageDriveFolder.id = f.id "
      + " JOIN Assay a ON a.id = af.assay.id "
      + " WHERE af.primary = true AND a.id = ?1")
  Optional<StorageDriveFolder> findPrimaryByAssayId(Long assayId);

}
