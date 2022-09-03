package io.studytracker.benchling.api;

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
import org.springframework.util.StringUtils;

public final class BenchlingNotebookUserService
    extends AbstractBenchlingApiService
    implements NotebookUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookUserService.class);


  @Override
  public List<NotebookUser> findNotebookUsers() {
    LOGGER.info("Fetching Benchling user list.");
    String authHeader = generateAuthorizationHeader();
    List<BenchlingUser> users = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingUserList userList = this.getClient().findUsers(authHeader, nextToken);
      users.addAll(userList.getUsers());
      nextToken = userList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return this.convertUsers(users);
  }

  @Override
  public Optional<NotebookUser> findNotebookUser(User user) {
    LOGGER.info("Looking up Benchling user: " + user.getDisplayName());
    String authHeader = generateAuthorizationHeader();

    // Lookup user by ID
    if (user.getConfiguration().containsKey(UserConfigurations.BENCHLING_USER_ID)) {
      String benchlingUserId = user.getConfiguration().get(UserConfigurations.BENCHLING_USER_ID);
      Optional<BenchlingUser> optional = this.getClient().findUserById(benchlingUserId, authHeader);
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
          this.getClient().findUsersByUsername(user.getEmail(), authHeader, nextToken);
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
        BenchlingUserList userList = this.getClient().findUsers(authHeader, nextToken);
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
