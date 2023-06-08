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

package io.studytracker.gitlab;

import io.studytracker.config.properties.GitProperties;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.git.GitServerGroup;
import io.studytracker.git.GitServerRepository;
import io.studytracker.git.GitServerUser;
import io.studytracker.git.GitService;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabProjectGroup;
import io.studytracker.model.*;
import io.studytracker.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GitLabService implements GitService<GitLabIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabService.class);

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private AssayRepository assayRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GitLabIntegrationService gitLabIntegrationService;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitLabRepositoryRepository gitLabRepositoryRepository;

  @Autowired
  private GitProperties gitProperties;

  @Override
  public List<GitServerGroup> listAvailableGroups(GitLabIntegration integration) {
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);
    return client.findGroups().stream()
        .map(GitLabUtils::toGitServerGroup)
        .collect(Collectors.toList());
  }

  @Override
  public Iterable<GitServerRepository> listAvailableRepositories(GitLabIntegration integration) {
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);
    return client.findProjects().stream()
        .map(GitLabUtils::toGitServerRepository)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<GitGroup> findRegisteredGroupById(Long id) {
    return gitGroupRepository.findById(id);
  }

  @Override
  public GitGroup updateRegisteredGroup(GitGroup gitGroup) {
    GitGroup g = gitGroupRepository.getById(gitGroup.getId());
    g.setDisplayName(gitGroup.getDisplayName());
    g.setWebUrl(gitGroup.getWebUrl());
    g.setActive(gitGroup.isActive());
    return gitGroupRepository.save(g);
  }

  @Override
  public void unregisterGroup(GitGroup group) {
    GitGroup g = gitGroupRepository.getById(group.getId());
    g.setActive(false);
    gitGroupRepository.save(g);
  }

  @Override
  public GitGroup registerGroup(GitLabIntegration integration, GitServerGroup group) {
    GitGroup gitGroup = new GitGroup();
    gitGroup.setOrganization(integration.getOrganization());
    gitGroup.setActive(true);
    gitGroup.setGitServiceType(GitServiceType.GITLAB);
    gitGroup.setDisplayName(group.getName());
    gitGroup.setWebUrl(group.getWebUrl());

    GitLabGroup gitLabGroup = new GitLabGroup();
    gitLabGroup.setGitGroup(gitGroup);
    gitLabGroup.setGitLabIntegration(integration);
    gitLabGroup.setGroupId(Integer.parseInt(group.getGroupId()));
    gitLabGroup.setName(group.getName());
    gitLabGroup.setPath(group.getPath());

    gitLabGroupRepository.save(gitLabGroup);
    GitLabGroup created = gitLabGroupRepository.findById(gitLabGroup.getId())
        .orElseThrow(() -> new RecordNotFoundException("GitLabGroup record not persisted."));
    LOGGER.info("Created root group {} ", group.getName());
    return created.getGitGroup();
  }

  @Override
  public List<GitGroup> findRegisteredGroups(GitLabIntegration integration) {
    return this.findRegisteredGroups(integration, false);
  }

  @Override
  public List<GitGroup> findRegisteredGroups(GitLabIntegration integration, boolean isRoot) {
    return gitGroupRepository.findByOrganizationId(integration.getOrganization().getId())
        .stream()
        .filter(g -> {
          if (isRoot) {
            return g.getParentGroup() == null;
          } else {
            return true;
          }
        })
        .collect(Collectors.toList());
  }

  private GitLabGroup saveProgramGroupRecord(GitGroup parentGroup, GitLabProjectGroup group,
      GitLabIntegration integration, Program program) {
    // Save the group records
    GitGroup gitGroup = new GitGroup();
    gitGroup.setParentGroup(parentGroup);
    gitGroup.setOrganization(parentGroup.getOrganization());
    gitGroup.setActive(true);
    gitGroup.setGitServiceType(GitServiceType.GITLAB);
    gitGroup.setDisplayName(program.getName() + " Program GitLab Project Group");
    gitGroup.setWebUrl(group.getWebUrl());

    GitLabGroup gitLabGroup = new GitLabGroup();
    gitLabGroup.setGitGroup(gitGroup);
    gitLabGroup.setGitLabIntegration(integration);
    gitLabGroup.setGroupId(group.getId());
    gitLabGroup.setName(group.getName());
    gitLabGroup.setPath(group.getPath());

    gitLabGroupRepository.save(gitLabGroup);
    GitLabGroup created = gitLabGroupRepository.findById(gitLabGroup.getId())
        .orElseThrow(() -> new RecordNotFoundException("GitLabGroup record not persisted."));
    LOGGER.info("Created group {} for program {}", created.getPath(), program.getName());
    return created;
  }

  @Override
  @Transactional
  public GitGroup createProgramGroup(GitGroup parentGroup, Program program) {

    LOGGER.info("Creating GitLab group for program {}", program.getName());

    // Get the Git client
    GitLabIntegration integration = gitLabIntegrationService.findByGitGroup(parentGroup)
        .orElseThrow(() -> new RecordNotFoundException("Integration not found for group "
            + parentGroup.getDisplayName()));
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);

    // Check to make sure a group doesn't already exist
    Optional<GitGroup> optional = this.findProgramGroup(parentGroup, program);
    if (optional.isPresent()) {
      LOGGER.info("Group already exists for program {}", program.getName());
      return optional.get();
    }

    // Get the parent GitLab group
    LOGGER.debug("Looking up root GitLab group: {}", parentGroup.getDisplayName());
    GitLabGroup parentGitLabGroup = gitLabGroupRepository.findByGitGroupId(parentGroup.getId());
    Optional<GitLabProjectGroup> parentGroupOptional = client.findGroupById(
        parentGitLabGroup.getGroupId());
    if (parentGroupOptional.isEmpty()) {
      throw new RecordNotFoundException("Root group not found. Check your GitLab configuration");
    }
    GitLabProjectGroup parentProjectGroup = parentGroupOptional.get();

    // Create the group
    GitLabNewGroupRequest request = new GitLabNewGroupRequest();
    request.setName(GitLabUtils.getProgramGroupName(program));
    request.setPath(GitLabUtils.getPathFromName(program.getName()));
    request.setAutoDevOpsEnabled(false);
    request.setDescription(program.getDescription() != null
        ? StringUtils.truncate(program.getDescription().replaceAll("<[^>]*>", ""), 255)
        : "Program " + program.getName() + " study group");
    request.setParentId(parentProjectGroup.getId());
    request.setVisibility(parentProjectGroup.getVisibility());
    GitLabProjectGroup group = client.createNewGroup(request);

    GitLabGroup created = this.saveProgramGroupRecord(parentGroup, group, integration, program);
    LOGGER.info("Created group {} for program {}", group.getPath(), program.getName());
    return created.getGitGroup();

  }

  @Override
  public Optional<GitGroup> findProgramGroup(GitGroup parentGroup, Program program) {
    LOGGER.debug("Finding GitLab group for program {} in parent group {}", program.getName(), parentGroup.getDisplayName());
    if (!program.getGitGroups().isEmpty()) {
      for (GitGroup group: gitGroupRepository.findByProgramId(program.getId())) {
        if (group.getParentGroup().getId().equals(parentGroup.getId())) {
          return Optional.of(group);
        }
      }
    } else {
      if (gitProperties.getUseExistingGroups()) {
        LOGGER.debug("Existing group not registered, now looking up existing GitLab group for program {}", program.getName());
        GitLabIntegration integration = gitLabIntegrationService.findByGitGroup(parentGroup)
            .orElseThrow(() -> new RecordNotFoundException("Integration not found for group "
                + parentGroup.getDisplayName()));
        GitLabRestClient client = GitLabClientFactory.createRestClient(integration);
        GitLabGroup parentGitLabGroup = gitLabGroupRepository.findByGitGroupId(parentGroup.getId());
        Optional<GitLabProjectGroup> optional = client.findGroups(program.getName()).stream()
            .filter(g -> g.getParentId().equals(parentGitLabGroup.getGroupId())
                && g.getName().equals(GitLabUtils.getProgramGroupName(program)))
            .findFirst();
        if (optional.isPresent()) {
          GitLabProjectGroup existingGroup = optional.get();
          GitLabGroup created = this.saveProgramGroupRecord(parentGroup, existingGroup, integration, program);
          return Optional.of(created.getGitGroup());
        }
      } else {
        LOGGER.debug("No existing GitLab group for program {}", program.getName());
      }
    }
    return Optional.empty();
  }

  @Override
  @Transactional
  public GitRepository createStudyRepository(GitGroup programGroup, Study study) {
    LOGGER.info("Creating repository for study {}", study.getName());

    // Get the Git client
    GitLabIntegration integration = gitLabIntegrationService.findByGitGroup(programGroup)
        .orElseThrow(() -> new RecordNotFoundException("Integration not found for group "
            + programGroup.getDisplayName()));
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);

    // Get the program group
    GitLabGroup gitLabProgramGroup = gitLabGroupRepository.findByGitGroupId(programGroup.getId());
    GitLabProjectGroup gitLabProjectGroup = client.findGroupById(gitLabProgramGroup.getGroupId())
        .orElseThrow(RecordNotFoundException::new);

    // Create the request
    String description = trimRepositoryDescription(study.getDescription());

    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setNamespaceId(gitLabProgramGroup.getGroupId());
    request.setName(GitLabUtils.getStudyProjectName(study));
    request.setPath(GitLabUtils.getStudyProjectPath(study));
    request.setDescription(description);
    request.setAutoDevopsEnabled(false);
    request.setInitializeWithReadme(false);
    request.setVisibility(gitLabProjectGroup.getVisibility());

    // Create the repository
    GitLabProject project = client.createProject(request);

    // Save the records
    GitRepository repository = new GitRepository();
    repository.setGitGroup(programGroup);
    repository.setDisplayName(project.getName());
    repository.setDescription(description);
    repository.setWebUrl(project.getWebUrl());
    repository.setHttpUrl(project.getHttpUrlToRepo());
    repository.setSshUrl(project.getSshUrlToRepo());

    GitLabRepository gitLabRepository = new GitLabRepository();
    gitLabRepository.setGitLabGroup(gitLabProgramGroup);
    gitLabRepository.setGitRepository(repository);
    gitLabRepository.setName(project.getName());
    gitLabRepository.setPath(project.getPath());
    gitLabRepository.setRepositoryId(project.getId());
    gitLabRepositoryRepository.save(gitLabRepository);

    LOGGER.info("Created repository {} for study {}", project.getPath(), study.getCode());

    GitRepository created = gitLabRepositoryRepository.findById(gitLabRepository.getId())
        .orElseThrow(() -> new RecordNotFoundException("GitLabRepository record not persisted."))
        .getGitRepository();
    study.addGitRepository(created);
    studyRepository.save(study);

    return created;

  }

//  @Override
//  public List<GitRepository> findStudyRepositories(GitGroup parentGroup, Study study) {
//    LOGGER.info("Getting repository for study {}", study.getName());
//
//    // Lookup by saved study attribute
//    if (study.getAttributes().containsKey(GitAttributes.REPOSITORY_ID)) {
//      Integer projectId = Integer.parseInt(study.getAttributes().get(GitAttributes.REPOSITORY_ID));
//      Optional<GitLabProject> optional = client.findProjectById(getAccessToken(), projectId);
//      if (optional.isPresent()) {
//        return Optional.of(GitLabUtils.toGitServerRepository(optional.get()));
//      } else {
//        LOGGER.warn("Saved repository ID {} not found for study {}. WIll try looking up repository by name.", projectId, study.getName());
//      }
//    }
//
//    // Lookup by name
//    List<GitLabProject> projects = client.findProjects(getAccessToken(), GitLabUtils.getStudyProjectPath(study));
//    for (GitLabProject project : projects) {
//      if (project.getPath().equals(GitLabUtils.getStudyProjectPath(study))) {
//        updateStudyRepositoryAttributes(study, project);
//        return Optional.of(GitLabUtils.toGitServerRepository(project));
//      }
//    }
//
//    return Optional.empty();
//  }

  @Override
  @Transactional
  public GitRepository createAssayRepository(GitGroup parentGroup, Assay assay) {

    LOGGER.info("Creating repository for assay {}", assay.getName());

    // Get the Git client
    GitLabIntegration integration = gitLabIntegrationService.findByGitGroup(parentGroup)
        .orElseThrow(() -> new RecordNotFoundException("Integration not found for group "
            + parentGroup.getDisplayName()));
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);

    // Get the program group
    Study study = studyRepository.findById(assay.getStudy().getId())
        .orElseThrow(RecordNotFoundException::new);
    Program program = programRepository.findById(study.getProgram().getId())
        .orElseThrow(RecordNotFoundException::new);
    Optional<GitGroup> optional = this.findProgramGroup(parentGroup, program);
    GitGroup programGroup = optional.orElseGet(() -> createProgramGroup(parentGroup, program));
    GitLabGroup gitLabProgramGroup = gitLabGroupRepository.findByGitGroupId(programGroup.getId());
    GitLabProjectGroup gitLabProjectGroup = client.findGroupById(gitLabProgramGroup.getGroupId())
        .orElseThrow(RecordNotFoundException::new);

    // Create the request
    String description = trimRepositoryDescription(assay.getDescription());
    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setNamespaceId(gitLabProgramGroup.getGroupId());
    request.setName(GitLabUtils.getAssayProjectName(assay));
    request.setPath(GitLabUtils.getAssayProjectPath(assay));
    request.setDescription(description);
    request.setAutoDevopsEnabled(false);
    request.setInitializeWithReadme(false);
    request.setVisibility(gitLabProjectGroup.getVisibility());

    // Create the repository
    GitLabProject project = client.createProject(request);

    // Save the records
    GitRepository repository = new GitRepository();
    repository.setGitGroup(programGroup);
    repository.setDisplayName(project.getName());
    repository.setDescription(description);
    repository.setWebUrl(project.getWebUrl());
    repository.setHttpUrl(project.getHttpUrlToRepo());
    repository.setSshUrl(project.getSshUrlToRepo());

    GitLabRepository gitLabRepository = new GitLabRepository();
    gitLabRepository.setGitLabGroup(gitLabProgramGroup);
    gitLabRepository.setGitRepository(repository);
    gitLabRepository.setName(project.getName());
    gitLabRepository.setPath(project.getPath());
    gitLabRepository.setRepositoryId(project.getId());
    gitLabRepositoryRepository.save(gitLabRepository);

    LOGGER.info("Created repository {} for assay {}", project.getPath(), assay.getCode());

    GitRepository created = gitLabRepositoryRepository.findById(gitLabRepository.getId())
        .orElseThrow(() -> new RecordNotFoundException("GitLabRepository record not persisted."))
        .getGitRepository();
    assay.addGitRepository(created);
    assayRepository.save(assay);

    LOGGER.info("Created repository {} for assay   {}", project.getPath(), assay.getCode());

    return created;
  }

//  @Override
//  public Optional<GitServerRepository> findAssayRepository(Assay assay) {
//    LOGGER.info("Getting repository for assay {}", assay.getName());
//    // Lookup by saved study attribute
//    if (assay.getAttributes().containsKey(GitAttributes.REPOSITORY_ID)) {
//      Integer projectId = Integer.parseInt(assay.getAttributes().get(GitAttributes.REPOSITORY_ID));
//      Optional<GitLabProject> optional = client.findProjectById(getAccessToken(), projectId);
//      if (optional.isPresent()) {
//        return Optional.of(GitLabUtils.toGitServerRepository(optional.get()));
//      } else {
//        LOGGER.warn("Saved repository ID {} not found for assay {}. WIll try looking up repository by name.", projectId, assay.getCode());
//      }
//    }
//
//    // Lookup by name
//    List<GitLabProject> projects = client.findProjects(getAccessToken(), GitLabUtils.getAssayProjectPath(assay));
//    for (GitLabProject project : projects) {
//      if (project.getPath().equals(GitLabUtils.getAssayProjectPath(assay))) {
//        updateAssayRepositoryAttributes(assay, project);
//        return Optional.of(GitLabUtils.toGitServerRepository(project));
//      }
//    }
//
//    return Optional.empty();
//  }

  @Override
  public Iterable<GitServerUser> listAvailableUsers(GitLabIntegration integration) {
    LOGGER.debug("Getting list of GitLab users");
    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);
    return client.findUsers().stream()
        .map(GitLabUtils::toGitServerUser)
        .collect(Collectors.toList());
  }

//  @Override
//  public Optional<GitServerUser> findUser(GitLabIntegration integration, User user) {
//    LOGGER.debug("Getting GitLab user for {}", user.getUsername());
//    GitLabRestClient client = GitLabClientFactory.createRestClient(integration);
//
//    // Lookup by saved user ID
//    if (user.getAttributes().containsKey(GitAttributes.USER_ID)) {
//      Integer userId = Integer.parseInt(user.getAttributes().get(GitAttributes.USER_ID));
//      Optional<GitLabUser> optional = client.findUserById(userId);
//      if (optional.isPresent()) {
//        return Optional.of(GitLabUtils.toGitServerUser(optional.get()));
//      } else {
//        LOGGER.warn("Saved user ID {} not found for user {}. WIll try looking up user by username.", userId, user.getUsername());
//      }
//    }
//
//    List<GitLabUser> users = client.findUsers(user.getUsername());
//    for (GitLabUser u : users) {
//      if (u.getUsername().equals(user.getUsername()) || u.getEmail().equals(user.getEmail())) {
//        updateUserAttributes(user, u);
//        return Optional.of(GitLabUtils.toGitServerUser(u));
//      }
//    }
//
//    LOGGER.warn("User not found for {}", user.getUsername());
//    return Optional.empty();
//  }

  private String trimRepositoryDescription(String description) {
    String cleaned = description.replaceAll("<[^>]*>", "");
    if (cleaned.length() > 255) {
      return description.substring(0, 252) + "...";
    }
    return cleaned;
  }

}
