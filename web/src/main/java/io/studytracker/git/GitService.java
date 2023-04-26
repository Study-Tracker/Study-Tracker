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

package io.studytracker.git;

import io.studytracker.model.Assay;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitRepository;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.Optional;

public interface GitService<T extends GitServerIntegration> {

  /**
   * Returns a list of all groups in the Git server.
   *
   * @param integration the Git server integration to use
   * @return group list
   */
  Iterable<GitServerGroup> listAvailableGroups(T integration);

  /**
   * Returns a list of all repositories in the Git server.
   *
   * @param integration the Git server integration to use
   * @return repository list
   */
  Iterable<GitServerRepository> listAvailableRepositories(T integration);

  /**
   * Returns a list of users in the Git server.
   *
   * @param integration the Git server integration to use
   * @return user list
   */
  Iterable<GitServerUser> listAvailableUsers(T integration);

  /**
   * Creates a new sub-group within the root group for the given Study Tracker program.
   *
   * @param integration the Git server integration to use
   * @param program the program to create a group for
   */
  GitGroup createProgramGroup(T integration, Program program);

  /**
   * Fetches an optional reference to an existing group for the given Study Tracker program.
   *
   * @param integration the Git server integration to use
   * @param program the program to fetch a group for
   * @return the group for the given program
   */
  Optional<GitGroup> findProgramGroup(T integration,Program program);

  /**
   * Creates a Git repository for the given study.
   *
   * @param integration the Git server integration to use
   * @param study the study to create a repository for
   * @return reference to the created repository
   */
  GitRepository createStudyRepository(T integration,Study study);

  /**
   * Fetches an optional reference to an existing repository for the given Study Tracker study.
   *
   * @param integration the Git server integration to use
   * @param study the study to fetch a repository for
   * @return the repository for the given study
   */
  Optional<GitRepository> findStudyRepository(T integration,Study study);

  /**
   * Creates a Git repository for the given assay.
   *
   * @param integration the Git server integration to use
   * @param assay the assay to create a repository for
   * @return reference to the created repository
   */
  GitRepository createAssayRepository(T integration,Assay assay);

  /**
   * Fetches reference to an existing repository for the given Study Tracker assay, if it exists.
   *
   * @param integration the Git server integration to use
   * @param assay the assay to fetch a repository for
   * @return the repository for the given assay
   */
  Optional<GitRepository> findAssayRepository(T integration, Assay assay);

  /**
   * Fetches the Git server user record for the given Study Tracker user.
   *
   * @param integration the Git server integration to use
   * @param user the user to fetch a Git server user for
   * @return the Git server user
   */
  Optional<GitServerUser> findUser(T integration, User user);

}
