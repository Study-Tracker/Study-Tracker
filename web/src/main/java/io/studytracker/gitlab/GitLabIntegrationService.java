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

import io.studytracker.gitlab.entities.GitLabProjectGroup;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitLabGroup;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.model.GitServiceType;
import io.studytracker.repository.GitLabGroupRepository;
import io.studytracker.repository.GitLabIntegrationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GitLabIntegrationService implements IntegrationService<GitLabIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabIntegrationService.class);

  @Autowired
  private GitLabIntegrationRepository gitLabIntegrationRepository;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Override
  public Optional<GitLabIntegration> findById(Long id) {
    LOGGER.debug("Find GitLabIntegration by id: {}", id);
    return gitLabIntegrationRepository.findById(id);
  }

  public Optional<GitLabIntegration> findByGitGroup(GitGroup group) {
    LOGGER.debug("Find GitLabIntegration by group: {}", group.getId());
    return gitLabIntegrationRepository.findByGitGroupId(group.getId());
  }

  @Override
  public List<GitLabIntegration> findAll() {
    LOGGER.debug("Find all GitLabIntegration");
    return gitLabIntegrationRepository.findAll();
  }

  @Override
  @Transactional
  public GitLabIntegration register(GitLabIntegration instance) {
    LOGGER.info("Registering GitLabIntegration");
    if (!validate(instance)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(instance)) {
      throw new IllegalArgumentException("Failed to connect to GitLab API with the provided credentials.");
    }
    instance.setActive(true);
    return gitLabIntegrationRepository.save(instance);
  }

  @Override
  @Transactional
  public GitLabIntegration update(GitLabIntegration instance) {
    LOGGER.info("Updating GitLabIntegration : {}",
        instance.getId());
    if (!validate(instance)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(instance)) {
      throw new IllegalArgumentException("Failed to connect to GitLab API with the provided credentials.");
    }
    GitLabIntegration i = gitLabIntegrationRepository.getById(instance.getId());
    i.setAccessToken(instance.getAccessToken());
    i.setName(instance.getName());
    i.setActive(instance.isActive());
    i.setRootUrl(instance.getRootUrl());
    i.setUsername(instance.getUsername());
    i.setPassword(instance.getPassword());
    return gitLabIntegrationRepository.save(i);
  }

  @Override
  public boolean validate(GitLabIntegration instance) {
    try {
      Assert.notNull(instance.getName(), "Name is required.");
      Assert.hasText(instance.getName(), "Name is required.");
      Assert.hasText(instance.getRootUrl(), "Root URL is required.");
      Assert.isTrue(StringUtils.hasText(instance.getAccessToken())
          || (StringUtils.hasText(instance.getUsername())
          && StringUtils.hasText(instance.getPassword())),
          "Either an access token or username and password are required.");
    } catch (Exception e) {
      LOGGER.error("Failed to validate GitLabIntegration: {}", e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public boolean test(GitLabIntegration instance) {
    try {
      GitLabRestClient client = GitLabClientFactory.createRestClient(instance);
      List<GitLabProjectGroup> groups = client.findGroups();
      return groups != null;
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Failed to test GitLabIntegration: {}", e.getMessage());
      return false;
    }
  }

  @Override
  @Transactional
  public void remove(GitLabIntegration instance) {
    LOGGER.info("Removing GitLabIntegration: {}", instance.getId());
    GitLabIntegration i = gitLabIntegrationRepository.getById(instance.getId());
    i.setActive(false);
    gitLabIntegrationRepository.save(i);
  }

  // Groups

  public Optional<GitLabGroup> findRootGroupById(Long id) {
    LOGGER.debug("Find GitLabGroup by id: {}", id);
    return gitLabGroupRepository.findById(id);
  }

  public List<GitLabGroup> findGroups(GitLabIntegration integration) {
    return this.findGroups(integration, false);
  }

  public List<GitLabGroup> findGroups(GitLabIntegration integration, boolean isRoot) {
    LOGGER.debug("Find GitLabGroup by integration: {}", integration.getId());
    return gitLabGroupRepository.findByIntegrationId(integration.getId())
        .stream()
        .filter(g -> {
          if (isRoot) return g.getGitGroup().getParentGroup() == null;
          else return true;
        })
        .collect(Collectors.toList());
  }
  
  @Transactional
  public GitLabGroup registerRootGroup(GitLabIntegration integration, GitLabGroup group) {
    LOGGER.info("Registering GitLabGroup: {}", group.getGroupId());
    group.setGitLabIntegration(integration);
    group.getGitGroup().setGitServiceType(GitServiceType.GITLAB);
    return gitLabGroupRepository.save(group);
  }

  @Transactional
  public GitLabGroup updateRootGroup(GitLabGroup group) {
    LOGGER.info("Updating GitLabGroup: {}", group.getGroupId());
    GitLabGroup g = gitLabGroupRepository.getById(group.getId());
    g.setGroupId(group.getGroupId());
    g.setName(group.getName());
    g.setPath(group.getPath());
    g.setGroupId(group.getGroupId());
    g.getGitGroup().setActive(group.getGitGroup().isActive());
    g.getGitGroup().setWebUrl(group.getGitGroup().getWebUrl());
    g.getGitGroup().setDisplayName(group.getGitGroup().getDisplayName());
    return gitLabGroupRepository.save(g);
  }

  @Transactional
  public void updateRootGroupStatus(Long groupId, boolean status) {
    LOGGER.info("Updating GitLabGroup status: {}", groupId);
    GitLabGroup g = gitLabGroupRepository.getById(groupId);
    g.getGitGroup().setActive(status);
    gitLabGroupRepository.save(g);
  }

  @Transactional
  public void removeRootGroup(GitLabGroup group) {
    GitLabGroup g = gitLabGroupRepository.getById(group.getId());
    g.getGitGroup().setActive(false);
    gitLabGroupRepository.save(g);
  }

}
