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
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.Optional;

public interface GitService {

  /**
   * Returns a list of all groups in the Git server.
   *
   * @return group list
   */
  Iterable<GitGroup> listGroups();

  /**
   * Returns a list of all repositories in the Git server.
   *
   * @return
   */
  Iterable<GitRepository> listRepositories();

  /**
   * Returns a list of users in the Git server.
   *
   * @return
   */
  Iterable<GitUser> listUsers();

  /**
   * Creates a new sub-group within the root group for the given Study Tracker program.
   *
   * @param program the program to create a group for
   */
  GitGroup createProgramGroup(Program program);

  /**
   * Fetches an optional reference to an existing group for the given Study Tracker program.
   *
   * @param program the program to fetch a group for
   * @return the group for the given program
   */
  Optional<GitGroup> findProgramGroup(Program program);

  /**
   * Creates a Git repository for the given study.
   *
   * @param study the study to create a repository for
   * @return reference to the created repository
   */
  GitRepository createStudyRepository(Study study);

  /**
   * Fetches an optional reference to an existing repository for the given Study Tracker study.
   *
   * @param study the study to fetch a repository for
   * @return the repository for the given study
   */
  Optional<GitRepository> findStudyRepository(Study study);

  /**
   * Creates a Git repository for the given assay.
   *
   * @param assay the assay to create a repository for
   * @return reference to the created repository
   */
  GitRepository createAssayRepository(Assay assay);

  /**
   * Fetches reference to an existing repository for the given Study Tracker assay, if it exists.
   *
   * @param assay the assay to fetch a repository for
   * @return the repository for the given assay
   */
  Optional<GitRepository> findAssayRepository(Assay assay);

  /**
   * Fetches the Git server user record for the given Study Tracker user.
   * @param user the user to fetch a Git server user for
   * @return the Git server user
   */
  Optional<GitUser> findUser(User user);

}
