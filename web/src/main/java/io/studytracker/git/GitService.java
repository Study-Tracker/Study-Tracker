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
import java.util.List;
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
   * Registers a {@link GitGroup} for the given {@link GitServerGroup} and {@link GitServerIntegration
   *
   * @param integration the Git server integration to use
   * @param group the Git server group to register
   * @return reference to the created group
   */
  GitGroup registerGroup(T integration, GitServerGroup group);

  /**
   * Lists all registered {@link GitGroup}s for the given {@link GitServerIntegration}.
   *
   * @param integration the Git server integration to use
   * @return list of registered groups
   */
  List<GitGroup> findRegisteredGroups(T integration);

  /**
   * Lists all registered {@link GitGroup}s for the given {@link GitServerIntegration}. Optionally, can
   *  return only root groups
   *
   * @param integration the Git server integration to use
   * @param isRoot whether to return only root groups
   * @return list of registered groups
   */
  List<GitGroup> findRegisteredGroups(T integration, boolean isRoot);

  /**
   * Fetches an optional reference to an existing {@link GitGroup} .
   *
   * @param id the PKID of the group to fetch
   * @return the group for the given Git server group
   */
  Optional<GitGroup> findRegisteredGroupById(Long id);

  /**
   * Updates the record for an existing {@link GitGroup}.
   * @param gitGroup the Git group to update
   * @return reference to the updated group
   */
  GitGroup updateRegisteredGroup(GitGroup gitGroup);

  /**
   * Removes the registration of a {@link GitGroup}.
   * @param group the Git server group to unregister
   */
  void unregisterGroup(GitGroup group);

  /**
   * Creates a new subgroup within a parent group for the given Study Tracker program.
   *
   * @param parentGroup the parent group in which to create a new subgroup for study repositories
   * @param program the program to create a group for
   */
  GitGroup createProgramGroup(GitGroup parentGroup, Program program);

  /**
   * Fetches an optional reference to an existing group for the given Study Tracker program.
   *
   * @param parentGroup the parent group in which to create a new subgroup for study repositories
   * @param program the program to fetch a group for
   * @return the group for the given program
   */
  Optional<GitGroup> findProgramGroup(GitGroup parentGroup, Program program);

  /**
   * Creates a Git repository for the given study.
   *
   * @param parentGroup the parent group in which to create the study repository
   * @param study the study to create a repository for
   * @return reference to the created repository
   */
  GitRepository createStudyRepository(GitGroup parentGroup, Study study);

  /**
   * Fetches an optional reference to an existing repository for the given Study Tracker study.
   *
   * @param parentGroup the parent group in which to check for the study repository
   * @param study the study to fetch a repository for
   * @return the repository for the given study
   */
//  List<GitRepository> findStudyRepositories(GitGroup parentGroup, Study study);

  /**
   * Creates a Git repository for the given assay.
   *
   * @param parentGroup the parent group in which to create the assay repository
   * @param assay the assay to create a repository for
   * @return reference to the created repository
   */
  GitRepository createAssayRepository(GitGroup parentGroup, Assay assay);

  /**
   * Fetches reference to an existing repository for the given Study Tracker assay, if it exists.
   *
   * @param parentGroup the parent group in which to check for the assay repository
   * @param assay the assay to fetch a repository for
   * @return the repository for the given assay
   */
//  List<GitRepository> findAssayRepositories(GitGroup parentGroup, Assay assay);

  /**
   * Fetches the Git server user record for the given Study Tracker user.
   *
   * @param integration the Git server integration to use
   * @param user the user to fetch a Git server user for
   * @return the Git server user
   */
//  Optional<GitServerUser> findUser(T integration, User user);

}
