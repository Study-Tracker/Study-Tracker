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
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.git.GitServerGroup;
import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitLabGroup;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.model.Organization;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.repository.GitLabGroupRepository;
import io.studytracker.service.OrganizationService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class GitLabIntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabIntegrationInitializer.class);

  @Autowired
  private StudyTrackerProperties properties;

  @Autowired
  private GitLabIntegrationService gitLabIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private GitLabService gitLabService;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Transactional
  public void initializeIntegrations() throws InvalidConfigurationException {

    Organization organization;

    // Get the current organization
    try {
      organization = organizationService.getCurrentOrganization();
    } catch (RecordNotFoundException e) {
      e.printStackTrace();
      LOGGER.warn("No organization found. Skipping initialization of GitLab integration.");
      return;
    }

    // Register the GitLab integration
    try {
      GitLabIntegration integration = registerGitLabIntegration(organization);
      if (properties.getGitlab().getRootGroupId() != null) {
        registerRootGroup(integration, properties.getGitlab().getRootGroupId());
        updateExistingGroups(integration);
      }
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.warn("Failed to initialize GitLab integration.");
      throw new InvalidConfigurationException(e);
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

  private GitLabIntegration registerGitLabIntegration(Organization organization) {

    LOGGER.info("Checking GitLab integration status...");
    GitLabProperties gitLabProperties = properties.getGitlab();

    if (gitLabProperties != null && gitLabProperties.getUrl() != null
        && (StringUtils.hasText(gitLabProperties.getAccessToken())
        || (StringUtils.hasText(gitLabProperties.getUsername())
        && StringUtils.hasText(gitLabProperties.getPassword())))) {

      List<GitLabIntegration> integrations = gitLabIntegrationService.findByOrganization(organization);

      if (integrations.size() > 0) {

        // Check to see if the integration record is already updated
        GitLabIntegration existing = integrations.get(0);
        if (!existing.getCreatedAt().equals(existing.getUpdatedAt())) {
          LOGGER.info("GitLab integration for organization {} is already configured. Skipping initialization.", organization.getName());
          return existing;
        }

        // If not, update it
        LOGGER.info("Updating GitLab integration for organization {}.", organization.getName());
        existing.setRootUrl(gitLabProperties.getUrl().toString());
        existing.setUsername(gitLabProperties.getUsername());
        existing.setPassword(gitLabProperties.getPassword());
        existing.setAccessToken(gitLabProperties.getAccessToken());
        existing.setActive(true);
        return gitLabIntegrationService.update(existing);

      } else {
        LOGGER.info("No GitLab integration found for organization {}. A new integration will be registered.", organization.getName());

        // Create a new integration record
        GitLabIntegration integration = new GitLabIntegration();
        integration.setActive(true);
        integration.setOrganization(organization);
        integration.setName("GitLab");
        integration.setRootUrl(gitLabProperties.getUrl().toString());
        integration.setUsername(gitLabProperties.getUsername());
        integration.setPassword(gitLabProperties.getPassword());
        integration.setAccessToken(gitLabProperties.getAccessToken());
        return gitLabIntegrationService.register(integration);

      }

    } else {
      LOGGER.info("No GitLab integration properties found for organization {}. Skipping initialization.", organization.getName());
    }

    return null;

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

}
