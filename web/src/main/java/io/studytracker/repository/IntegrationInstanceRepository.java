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

import io.studytracker.integration.IntegrationType;
import io.studytracker.model.IntegrationInstance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Deprecated
public interface IntegrationInstanceRepository extends JpaRepository<IntegrationInstance, Long> {

  List<IntegrationInstance> findByActive(Boolean active);

  Optional<IntegrationInstance> findByName(String name);

  Optional<IntegrationInstance> findByDisplayName(String displayName);

  @Query("select i from IntegrationInstance i where i.definition.id = ?1")
  List<IntegrationInstance> findByIntegrationDefinitionId(Long integrationDefinitionId);

  @Query("select i from IntegrationInstance  i where i.definition.type = ?1")
  List<IntegrationInstance> findByIntegrationType(IntegrationType type);

}
