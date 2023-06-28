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

package io.studytracker.service;

import io.studytracker.git.GitService;
import io.studytracker.git.GitServiceLookup;
import io.studytracker.model.Assay;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitRepository;
import io.studytracker.model.GitServiceType;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.repository.GitRepositoryRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GitRepositoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryService.class);

  @Autowired
  private GitRepositoryRepository gitRepositoryRepository;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private GitServiceLookup gitServiceLookup;

  public Optional<GitGroup> findProgramGitGroup(Program program) {
    LOGGER.debug("Looking up GitGroup for program: {}", program.getId());
    List<GitGroup> groups = gitGroupRepository.findByProgramId(program.getId());
    if (groups.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(groups.get(0));
    }
  }

  public Optional<GitRepository> findStudyGitRepository(Study study) {
    LOGGER.debug("Looking up GitRepository for study: {}", study.getId());
    List<GitRepository> repositories = gitRepositoryRepository.findByStudyId(study.getId());
    if (repositories.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(repositories.get(0));
    }
  }

  public Optional<GitRepository> findAssayGitRepository(Assay assay) {
    LOGGER.debug("Looking up GitRepository for assay: {}", assay.getId());
    List<GitRepository> repositories = gitRepositoryRepository.findByAssayId(assay.getId());
    if (repositories.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(repositories.get(0));
    }
  }

  public GitService<?> lookupGitService(GitServiceType type) {
    LOGGER.debug("Looking up GitService for type: {}", type);
    return gitServiceLookup.lookup(type)
        .orElseThrow(() -> new IllegalArgumentException("No GitService found for type: " + type));
  }

}
