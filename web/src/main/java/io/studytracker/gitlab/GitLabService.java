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

import io.studytracker.config.properties.GitLabProperties;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.git.GitAttributes;
import io.studytracker.git.GitGroup;
import io.studytracker.git.GitRepository;
import io.studytracker.git.GitService;
import io.studytracker.git.GitUser;
import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
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

  private GitLabProperties properties;

  private ProgramRepository programRepository;

  private StudyRepository studyRepository;

  private AssayRepository assayRepository;

  private UserRepository userRepository;

  private String getAccessToken() {
    if (StringUtils.hasText(properties.getAccessKey())) {
      return properties.getAccessKey();
    } else {
      GitLabAuthenticationToken token = client.authenticate();
      return token.getAccessToken();
    }
  }

  @Transactional
  void updateProgramGroupAttributes(Program program, GitLabGroup group, GitLabGroup parentGroup) {
    Program p = programRepository.findById(program.getId()).orElseThrow();
    p.getAttributes().put(GitAttributes.GIT_SERVICE, GitLabAttributes.GIT_SERVICE_VALUE);
    p.getAttributes().put(GitAttributes.GROUP_ID, group.getId().toString());
    p.getAttributes().put(GitAttributes.GROUP_NAME, group.getName());
    p.getAttributes().put(GitAttributes.GROUP_PATH, group.getPath());
    p.getAttributes().put(GitAttributes.GROUP_PARENT_ID, parentGroup.getId().toString());
    p.getAttributes().put(GitAttributes.GROUP_PARENT_NAME, parentGroup.getName());
    p.getAttributes().put(GitAttributes.GROUP_PARENT_PATH, parentGroup.getPath());
    programRepository.save(p);
  }

  @Transactional
  void updateUserAttributes(User user, GitLabUser gitLabUser) {
    User u = userRepository.findById(user.getId()).orElseThrow();
    u.getAttributes().put(GitAttributes.GIT_SERVICE, GitLabAttributes.GIT_SERVICE_VALUE);
    u.getAttributes().put(GitAttributes.USER_ID, gitLabUser.getId().toString());
    u.getAttributes().put(GitAttributes.USER_USERNAME, gitLabUser.getUsername());
    u.getAttributes().put(GitAttributes.USER_EMAIL, gitLabUser.getEmail());
    userRepository.save(u);
  }

  @Transactional
  void updateStudyRepositoryAttributes(Study study, GitLabProject project) {
    Study s = studyRepository.findById(study.getId()).orElseThrow();
    s.getAttributes().put(GitAttributes.GIT_SERVICE, GitLabAttributes.GIT_SERVICE_VALUE);
    s.getAttributes().put(GitAttributes.REPOSITORY_ID, project.getId().toString());
    s.getAttributes().put(GitAttributes.REPOSITORY_NAME, project.getName());
    s.getAttributes().put(GitAttributes.REPOSITORY_PATH, project.getPath());
    s.getAttributes().put(GitAttributes.REPOSITORY_SSH_URL, project.getSshUrlToRepo());
    s.getAttributes().put(GitAttributes.REPOSITORY_HTTP_URL, project.getHttpUrlToRepo());
    s.getAttributes().put(GitAttributes.REPOSITORY_WEB_URL, project.getWebUrl());
    studyRepository.save(s);
  }

  @Transactional
  void updateAssayRepositoryAttributes(Assay assay, GitLabProject project) {
    Assay a = assayRepository.findById(assay.getId()).orElseThrow();
    a.getAttributes().put(GitAttributes.GIT_SERVICE, GitLabAttributes.GIT_SERVICE_VALUE);
    a.getAttributes().put(GitAttributes.REPOSITORY_ID, project.getId().toString());
    a.getAttributes().put(GitAttributes.REPOSITORY_NAME, project.getName());
    a.getAttributes().put(GitAttributes.REPOSITORY_PATH, project.getPath());
    a.getAttributes().put(GitAttributes.REPOSITORY_SSH_URL, project.getSshUrlToRepo());
    a.getAttributes().put(GitAttributes.REPOSITORY_HTTP_URL, project.getHttpUrlToRepo());
    a.getAttributes().put(GitAttributes.REPOSITORY_WEB_URL, project.getWebUrl());
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

    // Get the parent group
    LOGGER.debug("Looking up root GitLab group: {}", properties.getRootGroupId());
    Optional<GitLabGroup> parentGroupOptional = client.findGroupById(token, properties.getRootGroupId());
    if (parentGroupOptional.isEmpty()) {
      throw new RecordNotFoundException("Root group not found. Check your GitLab configuration");
    }
    GitLabGroup parentGroup = parentGroupOptional.get();

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
    request.setDescription(program.getDescription() != null
        ? program.getDescription().replaceAll("<[^>]*>", "")
        : "Program " + program.getName() + " study group");
    request.setParentId(parentGroup.getId());
    request.setVisibility(parentGroup.getVisibility());
    GitLabGroup group = client.createNewGroup(token, request);
    LOGGER.info("Created group {} for program {}", group.getPath(), program.getName());

    // Update the program and set the namespace ID
    updateProgramGroupAttributes(program, group, parentGroup);

    return GitLabUtils.toGitGroup(group);

  }

  @Override
  public Optional<GitGroup> findProgramGroup(Program program) {
    LOGGER.info("Getting group for program {}", program.getName());
    String token = getAccessToken();

    // Lookup by saved group ID
    if (program.getAttributes().containsKey(GitAttributes.GROUP_ID)
        && StringUtils.hasText(program.getAttributes().get(GitAttributes.GROUP_ID))) {
      Integer groupId = Integer.parseInt(program.getAttributes().get(GitAttributes.GROUP_ID));
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
          Optional<GitLabGroup> parentGroupOptional = client.findGroupById(token, properties.getRootGroupId());
          if (parentGroupOptional.isEmpty()) {
            throw new RecordNotFoundException("Root group not found. Check your GitLab configuration");
          }
          GitLabGroup parentGroup = parentGroupOptional.get();
          updateProgramGroupAttributes(program, group, parentGroup);
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

    // Get the program group
    Program program = programRepository.findById(study.getProgram().getId())
        .orElseThrow(RecordNotFoundException::new);
    Optional<GitGroup> optional = this.findProgramGroup(program);
    GitGroup group = optional.orElseGet(() -> createProgramGroup(program));
    GitLabGroup gitLabGroup = client.findGroupById(getAccessToken(), Integer.parseInt(group.getGroupId()))
        .orElseThrow(RecordNotFoundException::new);

    // Create the request
    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setNamespaceId(Integer.parseInt(group.getGroupId()));
    request.setName(GitLabUtils.getStudyProjectName(study));
    request.setPath(GitLabUtils.getStudyProjectPath(study));
    request.setDescription(study.getDescription().replaceAll("<[^>]*>", ""));
    request.setAutoDevopsEnabled(false);
    request.setInitializeWithReadme(false);
    request.setVisibility(gitLabGroup.getVisibility());

    // Create the repository
    GitLabProject project = client.createProject(getAccessToken(), request);
    updateStudyRepositoryAttributes(study, project);
    LOGGER.info("Created repository {} for study {}", project.getPath(), study.getCode());

    return GitLabUtils.toGitRepository(project);

  }

  @Override
  public Optional<GitRepository> findStudyRepository(Study study) {
    LOGGER.info("Getting repository for study {}", study.getName());

    // Lookup by saved study attribute
    if (study.getAttributes().containsKey(GitAttributes.REPOSITORY_ID)) {
      Integer projectId = Integer.parseInt(study.getAttributes().get(GitAttributes.REPOSITORY_ID));
      Optional<GitLabProject> optional = client.findProjectById(getAccessToken(), projectId);
      if (optional.isPresent()) {
        return Optional.of(GitLabUtils.toGitRepository(optional.get()));
      } else {
        LOGGER.warn("Saved repository ID {} not found for study {}. WIll try looking up repository by name.", projectId, study.getName());
      }
    }

    // Lookup by name
    List<GitLabProject> projects = client.findProjects(getAccessToken(), GitLabUtils.getStudyProjectPath(study));
    for (GitLabProject project : projects) {
      if (project.getPath().equals(GitLabUtils.getStudyProjectPath(study))) {
        updateStudyRepositoryAttributes(study, project);
        return Optional.of(GitLabUtils.toGitRepository(project));
      }
    }

    return Optional.empty();
  }

  @Override
  public GitRepository createAssayRepository(Assay assay) {
    LOGGER.info("Creating repository for assay {}", assay.getName());
    // Get the program group
    Study study = studyRepository.findById(assay.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);
    Program program = programRepository.findById(study.getProgram().getId())
        .orElseThrow(RecordNotFoundException::new);
    Optional<GitGroup> optional = this.findProgramGroup(program);
    GitGroup group = optional.orElseGet(() -> createProgramGroup(program));
    GitLabGroup gitLabGroup = client.findGroupById(getAccessToken(), Integer.parseInt(group.getGroupId()))
        .orElseThrow(RecordNotFoundException::new);

    // Create the request
    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setNamespaceId(Integer.parseInt(group.getGroupId()));
    request.setName(GitLabUtils.getAssayProjectName(assay));
    request.setPath(GitLabUtils.getAssayProjectPath(assay));
    request.setDescription(assay.getDescription().replaceAll("<[^>]*>", ""));
    request.setAutoDevopsEnabled(false);
    request.setInitializeWithReadme(false);
    request.setVisibility(gitLabGroup.getVisibility());

    // Create the repository
    GitLabProject project = client.createProject(getAccessToken(), request);
    updateAssayRepositoryAttributes(assay, project);
    LOGGER.info("Created repository {} for assay   {}", project.getPath(), assay.getCode());

    return GitLabUtils.toGitRepository(project);
  }

  @Override
  public Optional<GitRepository> findAssayRepository(Assay assay) {
    LOGGER.info("Getting repository for assay {}", assay.getName());
    // Lookup by saved study attribute
    if (assay.getAttributes().containsKey(GitAttributes.REPOSITORY_ID)) {
      Integer projectId = Integer.parseInt(assay.getAttributes().get(GitAttributes.REPOSITORY_ID));
      Optional<GitLabProject> optional = client.findProjectById(getAccessToken(), projectId);
      if (optional.isPresent()) {
        return Optional.of(GitLabUtils.toGitRepository(optional.get()));
      } else {
        LOGGER.warn("Saved repository ID {} not found for assay {}. WIll try looking up repository by name.", projectId, assay.getCode());
      }
    }

    // Lookup by name
    List<GitLabProject> projects = client.findProjects(getAccessToken(), GitLabUtils.getAssayProjectPath(assay));
    for (GitLabProject project : projects) {
      if (project.getPath().equals(GitLabUtils.getAssayProjectPath(assay))) {
        updateAssayRepositoryAttributes(assay, project);
        return Optional.of(GitLabUtils.toGitRepository(project));
      }
    }

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
    if (user.getAttributes().containsKey(GitAttributes.USER_ID)) {
      Integer userId = Integer.parseInt(user.getAttributes().get(GitAttributes.USER_ID));
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
  public void setProperties(GitLabProperties properties) {
    this.properties = properties;
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
