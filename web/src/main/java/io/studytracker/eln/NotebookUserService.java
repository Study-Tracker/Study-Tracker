/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.eln;

import io.studytracker.model.User;
import java.util.List;
import java.util.Optional;

public interface NotebookUserService {

  /**
   * Returns a list of all users registered in the ELN.
   *
   * @return
   */
  List<NotebookUser> findNotebookUsers();

  /**
   * Attempts to find a Study Tracker user in the ELN.
   *
   * @param user
   * @return
   */
  Optional<NotebookUser> findNotebookUser(User user);
}
