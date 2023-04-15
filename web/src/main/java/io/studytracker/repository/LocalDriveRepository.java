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

import io.studytracker.model.LocalDrive;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface LocalDriveRepository extends StorageDriveDetailsOperations<LocalDrive> {

  @Override
  @Query("SELECT l FROM LocalDrive l WHERE l.organization.id = ?1")
  List<LocalDrive> findByOrganizationId(Long organizationId);

  @Override
  default List<LocalDrive> findByIntegrationId(Long integrationId) { return new ArrayList<>(); }

  @Override
  @Query("SELECT l FROM LocalDrive l WHERE l.storageDrive.id = ?1")
  Optional<LocalDrive> findByStorageDriveId(Long storageDriveId);

}
