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

package io.studytracker.benchling;

import io.studytracker.benchling.api.AbstractBenchlingApiService;
import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.BenchlingUser;
import io.studytracker.benchling.api.entities.BenchlingUserList;
import io.studytracker.eln.NotebookUser;
import io.studytracker.eln.NotebookUserService;
import io.studytracker.model.User;
import io.studytracker.model.UserConfigurations;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public final class BenchlingNotebookUserService
    extends AbstractBenchlingApiService
    implements NotebookUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookUserService.class);


  @Override
  public List<NotebookUser> findNotebookUsers() {
    LOGGER.info("Fetching Benchling user list.");
    List<BenchlingUser> users = new ArrayList<>();
    BenchlingElnRestClient client = this.getClient();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingUserList userList = client.findUsers(nextToken);
      users.addAll(userList.getUsers());
      nextToken = userList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return this.convertUsers(users);
  }

  @Override
  public Optional<NotebookUser> findNotebookUser(User user) {
    LOGGER.info("Looking up Benchling user: " + user.getDisplayName());
    BenchlingElnRestClient client = this.getClient();

    // Lookup user by ID
    if (user.getAttributes() != null
        && user.getAttributes().containsKey(UserConfigurations.BENCHLING_USER_ID)) {
      String benchlingUserId = user.getAttributes().get(UserConfigurations.BENCHLING_USER_ID);
      Optional<BenchlingUser> optional = client.findUserById(benchlingUserId);
      if (optional.isPresent()) {
        return Optional.of(this.convertUser(optional.get()));
      }
    }

    // Look up user by username
    List<BenchlingUser> users = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingUserList userList =
          client.findUsersByUsername(user.getEmail().replaceAll("@.+", ""), nextToken);
      users.addAll(userList.getUsers());
      nextToken = userList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    if (!users.isEmpty()) {
      for (BenchlingUser benchlingUser : users) {
        if (benchlingUser.getEmail().equals(user.getEmail())) {
          return Optional.of(this.convertUser(benchlingUser));
        }
      }
    }

    // Otherwise, check all the users
    else {
      nextToken = null;
      hasNext = true;
      while (hasNext) {
        BenchlingUserList userList = client.findUsers(nextToken);
        users.addAll(userList.getUsers());
        nextToken = userList.getNextToken();
        hasNext = StringUtils.hasText(nextToken);
      }
      if (!users.isEmpty()) {
        for (BenchlingUser benchlingUser : users) {
          if (benchlingUser.getEmail().equals(user.getEmail())) {
            return Optional.of(this.convertUser(benchlingUser));
          }
        }
      }
    }

    return Optional.empty();
  }

  private NotebookUser convertUser(BenchlingUser benchlingUser) {
    NotebookUser notebookUser = new NotebookUser();
    notebookUser.setName(benchlingUser.getName());
    notebookUser.setUsername(benchlingUser.getHandle());
    notebookUser.setEmail(benchlingUser.getEmail());
    notebookUser.setReferenceId(benchlingUser.getId());
    return notebookUser;
  }

  private List<NotebookUser> convertUsers(List<BenchlingUser> benchlingUsers) {
    List<NotebookUser> notebookUsers = new ArrayList<>();
    for (BenchlingUser benchlingUser : benchlingUsers) {
      notebookUsers.add(this.convertUser(benchlingUser));
    }
    return notebookUsers;
  }


}
