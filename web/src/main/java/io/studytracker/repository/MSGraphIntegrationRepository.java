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

import io.studytracker.model.MSGraphIntegration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MSGraphIntegrationRepository extends JpaRepository<MSGraphIntegration, Long> {

  @Query("SELECT i FROM MSGraphIntegration i WHERE i.organization.id = ?1")
  List<MSGraphIntegration> findByOrganizationId(Long organizationId);

  @Query("SELECT i FROM MSGraphIntegration i "
      + " JOIN OneDriveDrive d ON d.msgraphIntegration.id = i.id "
      + " JOIN StorageDrive sd ON d.storageDrive.id = sd.id "
      + " WHERE sd.id = ?1")
  Optional<MSGraphIntegration> findByStorageDriveId(Long storageDriveId);

  @Query("SELECT i FROM MSGraphIntegration i "
      + " JOIN OneDriveDrive d ON d.msgraphIntegration.id = i.id "
      + " WHERE d.id = ?1")
  Optional<MSGraphIntegration> findByOneDriveDriveId(Long oneDriveDriveId);

}