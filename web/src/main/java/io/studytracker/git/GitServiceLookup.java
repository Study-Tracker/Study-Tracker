package io.studytracker.git;

import io.studytracker.gitlab.GitLabService;
import io.studytracker.model.GitServiceType;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GitServiceLookup {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceLookup.class);

  @Autowired(required = false)
  private GitLabService gitLabService;

  public Optional<GitService> lookup(GitServiceType gitServiceType) {
    LOGGER.debug("Looking up GitService for gitServiceType: {}", gitServiceType);
    switch (gitServiceType) {
      case GITLAB:
        return Optional.ofNullable(gitLabService);
      default:
        return Optional.empty();
    }
  }

}
