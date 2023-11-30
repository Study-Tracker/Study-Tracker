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

package io.studytracker.config.initialization;

import io.studytracker.config.properties.GitLabProperties;
import io.studytracker.config.properties.StudyTrackerProperties;
import io.studytracker.exception.InvalidConfigurationException;
import io.studytracker.git.GitServerGroup;
import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitLabGroup;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.repository.GitLabGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class GitLabIntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabIntegrationInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private GitLabIntegrationService gitLabIntegrationService;

  @Autowired
  private GitLabService gitLabService;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    // Register the GitLab integration
    LOGGER.info("Checking GitLab integration status...");
    GitLabProperties gitLabProperties = properties.getGitlab();

    if (gitLabProperties != null && gitLabProperties.getUrl() != null
        && (StringUtils.hasText(gitLabProperties.getAccessToken())
        || (StringUtils.hasText(gitLabProperties.getUsername())
        && StringUtils.hasText(gitLabProperties.getPassword())))) {

      List<GitLabIntegration> integrations = gitLabIntegrationService.findAll();
      try {
        if (integrations.size() > 0) {
//          updateExistingIntegration(integrations.get(0));
          LOGGER.warn("GitLab integration is already configured. Skipping initialization.");
        } else {
          registerNewGitLabIntegration();
        }
      } catch (Exception e) {
        e.printStackTrace();
        LOGGER.warn("Failed to initialize GitLab integration.");
        throw new InvalidConfigurationException(e);
      }

    } else {
      LOGGER.info("No GitLab integration properties found. Skipping initialization.");
    }

  }

  private void registerNewGitLabIntegration() {
    LOGGER.info("No GitLab integration found. A new integration will be registered.");
    GitLabProperties gitLabProperties = properties.getGitlab();
    GitLabIntegration integration = new GitLabIntegration();
    integration.setActive(true);
    integration.setName("GitLab");
    integration.setRootUrl(gitLabProperties.getUrl().toString());
    integration.setUsername(gitLabProperties.getUsername());
    integration.setPassword(gitLabProperties.getPassword());
    integration.setAccessToken(gitLabProperties.getAccessToken());
    GitLabIntegration created = gitLabIntegrationService.register(integration);

    if (gitLabProperties.getRootGroupId() != null) {
      registerRootGroup(created, gitLabProperties.getRootGroupId());
      updateExistingGroups(created);
    }


  }

  private void updateExistingIntegration(GitLabIntegration existing) {
    GitLabProperties gitLabProperties = properties.getGitlab();
    if (!existing.getCreatedAt().equals(existing.getUpdatedAt())) {
      LOGGER.info("GitLab integration is already configured. Skipping initialization.");
    } else {
      LOGGER.info("Updating GitLab integration {}.", existing.getId());
      existing.setRootUrl(gitLabProperties.getUrl().toString());
      existing.setUsername(gitLabProperties.getUsername());
      existing.setPassword(gitLabProperties.getPassword());
      existing.setAccessToken(gitLabProperties.getAccessToken());
      existing.setActive(true);
      GitLabIntegration updated = gitLabIntegrationService.update(existing);

      if (gitLabProperties.getRootGroupId() != null) {
        registerRootGroup(updated, gitLabProperties.getRootGroupId());
        updateExistingGroups(updated);
      }
    }
  }

  private GitGroup registerRootGroup(GitLabIntegration integration, Integer rootGroupId) {

    // Find the GitLab server group that matches the config property
    Optional<GitServerGroup> optional = gitLabService.listAvailableGroups(integration)
        .stream()
        .filter(g -> g.getGroupId().equals(rootGroupId.toString()))
        .findFirst();
    if (optional.isPresent()) {

      // Check to see if it is already registered
      GitServerGroup serverGroup = optional.get();
      GitLabGroup rootGitLabGroup = null;
      for (GitGroup rootGroup: gitLabService.findRegisteredGroups(integration)){
        GitLabGroup glGroup = gitLabGroupRepository.findByGitGroupId(rootGroup.getId());
        if (glGroup.getGroupId().toString().equals(serverGroup.getGroupId())) {
          rootGitLabGroup = glGroup;
          break;
        }
      }

      // No group is currently registered
      if (rootGitLabGroup == null) {
        LOGGER.info("Registering root GitLab group: {}", serverGroup.getName());
        return gitLabService.registerGroup(integration, serverGroup);
      }

      // A group is already registered
      else {
        return rootGitLabGroup.getGitGroup();
      }

    } else {
      LOGGER.warn("Root group with ID {} not found. Skipping initialization of root group.", rootGroupId);
      return null;
    }
  }

  private void updateExistingGroups(GitLabIntegration integration) {
    for (GitLabGroup gitLabGroup: gitLabGroupRepository.findByIntegrationId(integration.getId())){
      GitGroup gitGroup = gitLabGroup.getGitGroup();
      if (gitGroup.getCreatedAt().equals(gitGroup.getUpdatedAt())
          && gitGroup.getWebUrl().equals("PLACEHOLDER")) {

        Optional<GitServerGroup> optional = gitLabService.listAvailableGroups(integration)
            .stream()
            .filter(g -> g.getGroupId().equals(gitLabGroup.getGroupId().toString()))
            .findFirst();

        if (optional.isPresent()) {
          GitServerGroup serverGroup = optional.get();
          gitLabGroup.setName(serverGroup.getName());
          gitLabGroup.setPath(serverGroup.getPath());
          gitLabGroup.getGitGroup().setDisplayName(serverGroup.getName());
          gitLabGroup.getGitGroup().setWebUrl(serverGroup.getWebUrl());
          gitLabGroupRepository.save(gitLabGroup);
        } else {
          LOGGER.warn("GitLab group {} not found. Skipping update.", gitLabGroup.getGroupId());
        }

      }
    }
  }

}
