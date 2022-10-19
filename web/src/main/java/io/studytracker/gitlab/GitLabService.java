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

package io.studytracker.gitlab;

import io.studytracker.git.GitGroup;
import io.studytracker.git.GitRepository;
import io.studytracker.git.GitService;
import io.studytracker.git.GitUser;
import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabUser;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

public class GitLabService implements GitService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabService.class);

  private GitLabRestClient client;

  private GitLabOptions options;

  private ProgramRepository programRepository;

  private StudyRepository studyRepository;

  private AssayRepository assayRepository;

  private UserRepository userRepository;

  private String getAccessToken() {
    if (StringUtils.hasText(options.getAccessToken())) {
      return options.getAccessToken();
    } else {
      GitLabAuthenticationToken token = client.authenticate();
      return token.getAccessToken();
    }
  }

  @Transactional
  void updateProgramGroupAttributes(Program program, GitLabGroup group) {
    Program p = programRepository.findById(program.getId()).orElseThrow();
    p.getAttributes().put(GitLabAttributes.NAMESPACE_ID, group.getId().toString());
    p.getAttributes().put(GitLabAttributes.NAMESPACE_NAME, group.getName());
    p.getAttributes().put(GitLabAttributes.NAMESPACE_PATH, group.getPath());
    programRepository.save(p);
  }

  @Transactional
  void updateUserAttributes(User user, GitLabUser gitLabUser) {
    User u = userRepository.findById(user.getId()).orElseThrow();
    u.getAttributes().put(GitLabAttributes.USER_ID, gitLabUser.getId().toString());
    u.getAttributes().put(GitLabAttributes.USER_USERNAME, gitLabUser.getUsername());
    u.getAttributes().put(GitLabAttributes.USER_EMAIL, gitLabUser.getEmail());
    userRepository.save(u);
  }

  @Transactional
  void updateStudyRepositoryAttributes(Study study, GitLabProject project) {
    Study s = studyRepository.findById(study.getId()).orElseThrow();
    s.getAttributes().put(GitLabAttributes.REPOSITORY_ID, project.getId().toString());
    s.getAttributes().put(GitLabAttributes.REPOSITORY_NAME, project.getName());
    s.getAttributes().put(GitLabAttributes.REPOSITORY_PATH, project.getPath());
    s.getAttributes().put(GitLabAttributes.REPOSITORY_SSH_URL, project.getSshUrlToRepo());
    s.getAttributes().put(GitLabAttributes.REPOSITORY_HTTP_URL, project.getHttpUrlToRepo());
    s.getAttributes().put(GitLabAttributes.REPOSITORY_WEB_URL, project.getWebUrl());
    studyRepository.save(s);
  }

  @Transactional
  void updateAssayRepositoryAttributes(Assay assay, GitLabProject project) {
    Assay a = assayRepository.findById(assay.getId()).orElseThrow();
    a.getAttributes().put(GitLabAttributes.REPOSITORY_ID, project.getId().toString());
    a.getAttributes().put(GitLabAttributes.REPOSITORY_NAME, project.getName());
    a.getAttributes().put(GitLabAttributes.REPOSITORY_PATH, project.getPath());
    a.getAttributes().put(GitLabAttributes.REPOSITORY_SSH_URL, project.getSshUrlToRepo());
    a.getAttributes().put(GitLabAttributes.REPOSITORY_HTTP_URL, project.getHttpUrlToRepo());
    a.getAttributes().put(GitLabAttributes.REPOSITORY_WEB_URL, project.getWebUrl());
    assayRepository.save(a);
  }

  @Override
  public List<GitGroup> listGroups() {
    return client.findGroups(getAccessToken()).stream()
        .map(GitLabUtils::toGitGroup)
        .collect(Collectors.toList());
  }

  @Override
  public Iterable<GitRepository> listRepositories() {
    return client.findProjects(getAccessToken()).stream()
        .map(GitLabUtils::toGitRepository)
        .collect(Collectors.toList());
  }

  @Override
  public GitGroup createProgramGroup(Program program) {

    LOGGER.info("Creating group for program {}", program.getName());
    String token = getAccessToken();

    // Check to make sure a group doesn't already exist
    Optional<GitGroup> optional = this.findProgramGroup(program);
    if (optional.isPresent()) {
      LOGGER.info("Group already exists for program {}", program.getName());
      return optional.get();
    }

    // Create the group
    GitLabNewGroupRequest request = new GitLabNewGroupRequest();
    request.setName(program.getName());
    request.setPath(GitLabUtils.getPathFromName(program.getName()));
    request.setAutoDevOpsEnabled(false);
    request.setDescription(program.getDescription());
    request.setParentId(options.getRootGroupId());
    GitLabGroup group = client.createNewGroup(token, request);
    LOGGER.info("Created group {} for program {}", group.getPath(), program.getName());

    // Update the program and set the namespace ID
    updateProgramGroupAttributes(program, group);

    return GitLabUtils.toGitGroup(group);

  }

  @Override
  public Optional<GitGroup> findProgramGroup(Program program) {
    LOGGER.info("Getting group for program {}", program.getName());
    String token = getAccessToken();

    // Lookup by saved group ID
    if (program.getAttributes().containsKey(GitLabAttributes.NAMESPACE_ID)
        && StringUtils.hasText(program.getAttributes().get(GitLabAttributes.NAMESPACE_ID))) {
      Integer groupId = Integer.parseInt(program.getAttributes().get(GitLabAttributes.NAMESPACE_ID));
      Optional<GitLabGroup> optional = client.findGroupById(token, groupId);
      if (optional.isPresent()) {
        return Optional.of(GitLabUtils.toGitGroup(optional.get()));
      } else {
        LOGGER.warn("Saved group ID {} not found for program {}. WIll try looking up group by name.", groupId, program.getName());
      }
    }

    // Lookup by name
    List<GitLabGroup> groups = client.findGroups(token, GitLabUtils.getPathFromName(program.getName()));
    if (!groups.isEmpty()) {
      for (GitLabGroup group : groups) {
        if (group.getPath().equals(GitLabUtils.getPathFromName(program.getName()))) {
          updateProgramGroupAttributes(program, group);
          return Optional.of(GitLabUtils.toGitGroup(group));
        }
      }
    }

    LOGGER.warn("Group not found for program " + program.getName());
    return Optional.empty();
  }

  @Override
  public GitRepository createStudyRepository(Study study) {
    LOGGER.info("Creating repository for study {}", study.getName());
    return null;
  }

  @Override
  public Optional<GitRepository> findStudyRepository(Study study) {
    LOGGER.info("Getting repository for study {}", study.getName());

    return Optional.empty();
  }

  @Override
  public GitRepository createAssayRepository(Assay assay) {
    LOGGER.info("Creating repository for assay {}", assay.getName());
    return null;
  }

  @Override
  public Optional<GitRepository> findAssayRepository(Assay assay) {
    LOGGER.info("Getting repository for assay {}", assay.getName());
    return Optional.empty();
  }

  @Override
  public Iterable<GitUser> listUsers() {
    LOGGER.debug("Getting list of GitLab users");
    return client.findUsers(getAccessToken()).stream()
        .map(GitLabUtils::toGitUser)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<GitUser> findUser(User user) {
    LOGGER.debug("Getting GitLab user for {}", user.getUsername());

    // Lookup by saved user ID
    if (user.getAttributes().containsKey(GitLabAttributes.USER_ID)) {
      Integer userId = Integer.parseInt(user.getAttributes().get(GitLabAttributes.USER_ID));
      Optional<GitLabUser> optional = client.findUserById(getAccessToken(), userId);
      if (optional.isPresent()) {
        return Optional.of(GitLabUtils.toGitUser(optional.get()));
      } else {
        LOGGER.warn("Saved user ID {} not found for user {}. WIll try looking up user by username.", userId, user.getUsername());
      }
    }

    List<GitLabUser> users = client.findUsers(getAccessToken(), user.getUsername());
    for (GitLabUser u : users) {
      if (u.getUsername().equals(user.getUsername()) || u.getEmail().equals(user.getEmail())) {
        updateUserAttributes(user, u);
        return Optional.of(GitLabUtils.toGitUser(u));
      }
    }

    LOGGER.warn("User not found for {}", user.getUsername());
    return Optional.empty();
  }

  @Autowired
  public void setClient(GitLabRestClient client) {
    this.client = client;
  }

  @Autowired
  public void setOptions(GitLabOptions options) {
    this.options = options;
  }

  @Autowired
  public void setProgramRepository(ProgramRepository programRepository) {
    this.programRepository = programRepository;
  }

  @Autowired
  public void setStudyRepository(StudyRepository studyRepository) {
    this.studyRepository = studyRepository;
  }

  @Autowired
  public void setAssayRepository(AssayRepository assayRepository) {
    this.assayRepository = assayRepository;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
}
