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

import io.studytracker.model.GitLabIntegration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GitLabIntegrationRepository extends JpaRepository<GitLabIntegration, Long> {

  @Query("select i "
      + " from GitGroup g "
      + " join GitLabGroup gg on gg.gitGroup.id = g.id "
      + " join GitLabIntegration i on i.id = gg.gitLabIntegration.id "
      + " where g.id = ?1")
  Optional<GitLabIntegration> findByGitGroupId(Long gitGroupId);

  @Query("select i from GitLabGroup g "
      + " join GitLabIntegration i on i.id = g.gitLabIntegration.id "
      + " where g.id = ?1")
  GitLabIntegration findByGitLabGroupId(Long gitLabGroupId);

}
