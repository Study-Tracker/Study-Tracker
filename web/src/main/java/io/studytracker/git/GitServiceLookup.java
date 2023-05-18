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
