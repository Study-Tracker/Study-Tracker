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

import io.studytracker.model.EgnyteDrive;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface EgnyteDriveRepository extends StorageDriveDetailsOperations<EgnyteDrive> {

  @Override
  @Query("SELECT d FROM EgnyteDrive d WHERE d.egnyteIntegration.id = ?1")
  List<EgnyteDrive> findByIntegrationId(Long integrationId);

  @Override
  @Query("SELECT d FROM EgnyteDrive d JOIN EgnyteIntegration i ON d.egnyteIntegration.id = i.id WHERE i.organization.id = ?1")
  List<EgnyteDrive> findByOrganizationId(Long integrationId);

  @Override
  @Query("SELECT d FROM EgnyteDrive d WHERE d.storageDrive.id = ?1")
  Optional<EgnyteDrive> findByStorageDriveId(Long id);

}
