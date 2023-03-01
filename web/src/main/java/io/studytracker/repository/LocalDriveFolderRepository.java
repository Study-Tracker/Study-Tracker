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

import io.studytracker.model.LocalDriveFolder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocalDriveFolderRepository
    extends JpaRepository<LocalDriveFolder, Long>, StorageDriveFolderDetailsOperations<LocalDriveFolder> {

  @Override
  @Query("SELECT f FROM LocalDriveFolder f WHERE f.storageDriveFolder.id = ?1")
  Optional<LocalDriveFolder> findByStorageDriveFolderId(Long id);

}
