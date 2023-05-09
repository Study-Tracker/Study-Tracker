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

package io.studytracker.controller.api.internal;

import io.studytracker.git.GitServerGroup;
import io.studytracker.git.GitServiceLookup;
import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.mapstruct.dto.form.GitLabGroupFormDto;
import io.studytracker.mapstruct.dto.form.GitLabIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.GitGroupDetailsDto;
import io.studytracker.mapstruct.dto.response.GitLabGroupDetailsDto;
import io.studytracker.mapstruct.dto.response.GitLabIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.GitGroupMapper;
import io.studytracker.mapstruct.mapper.GitLabGroupMapper;
import io.studytracker.mapstruct.mapper.GitLabIntegrationMapper;
import io.studytracker.model.GitLabGroup;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.model.GitServiceType;
import io.studytracker.model.Organization;
import io.studytracker.service.OrganizationService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/integrations/gitlab")
public class GitLabIntegrationPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabIntegrationPrivateController.class);

  @Autowired
  private GitLabIntegrationService gitLabIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private GitLabIntegrationMapper gitLabIntegrationMapper;

  @Autowired
  private GitGroupMapper gitGroupMapper;

  @Autowired
  private GitLabGroupMapper gitLabGroupMapper;

  @Autowired
  private GitServiceLookup gitServiceLookup;

  @GetMapping("")
  public List<GitLabIntegrationDetailsDto> fetchIntegrations() {
    LOGGER.debug("Fetching GitLab integrations");
    Organization organization = organizationService.getCurrentOrganization();
    List<GitLabIntegration> integrations = (List<GitLabIntegration>) gitLabIntegrationService
        .findByOrganization(organization);
    return gitLabIntegrationMapper.toDetailsDto(integrations);
  }

  @PostMapping("")
  public HttpEntity<GitLabIntegrationDetailsDto> registerIntegration(
      @Valid @RequestBody GitLabIntegrationFormDto dto) {
    Organization organization = organizationService.getCurrentOrganization();
    LOGGER.info("Registering GitLab integration for organization: {}", organization.getId());
    GitLabIntegration integration = gitLabIntegrationMapper.fromFormDto(dto);
    integration.setOrganization(organization);
    GitLabIntegration created = gitLabIntegrationService.register(integration);
    return new ResponseEntity<>(gitLabIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<GitLabIntegrationDetailsDto> updateRegistration(
      @PathVariable("id") Long integrationId, @Valid @RequestBody GitLabIntegrationFormDto dto) {
    LOGGER.info("Updating GitLab integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration existing = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!existing.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabIntegration updated = gitLabIntegrationService.update(gitLabIntegrationMapper.fromFormDto(dto));
    return new ResponseEntity<>(gitLabIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> removeRegistration(
      @PathVariable("id") Long integrationId) {
    LOGGER.info("Removing GitLab integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration existing = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!existing.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    gitLabIntegrationService.remove(existing);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Groups

  @GetMapping("/{id}/groups/available")
  public List<GitServerGroup> findAvailableGroups(@PathVariable("id") Long integrationId,
      @RequestParam(name = "q", required = false) String query) {
    LOGGER.debug("Fetching GitLab available groups for integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabService gitLabService = (GitLabService) gitServiceLookup.lookup(GitServiceType.GITLAB)
        .orElseThrow(() -> new IllegalStateException("GitLab service not found"));
    return gitLabService.listAvailableGroups(integration).stream()
        .filter(g -> {
          if (!StringUtils.hasText(query)) return true;
          else return g.getName().toLowerCase().contains(query.toLowerCase());
        })
        .collect(Collectors.toList());

  }

  @GetMapping("/{id}/groups/registered")
  public List<GitGroupDetailsDto> findRegisteredGroups(@PathVariable("id") Long integrationId,
      @RequestParam(value = "root", required = false) boolean isRoot) {
    LOGGER.debug("Fetching GitLab root groups for integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabService gitLabService = (GitLabService) gitServiceLookup.lookup(GitServiceType.GITLAB)
        .orElseThrow(() -> new IllegalStateException("GitLab service not found"));
    return gitGroupMapper.toDetailsDto(gitLabService.findRegisteredGroups(integration, isRoot));
  }

  @PostMapping("/{id}/groups/registered")
  public HttpEntity<GitLabGroupDetailsDto> registerRootGroup(@PathVariable("id") Long integrationId,
      @RequestBody @Valid GitLabGroupFormDto dto) {
    LOGGER.info("Registering GitLab root group for integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabGroup created = gitLabIntegrationService.registerRootGroup(integration, gitLabGroupMapper.fromFormDto(dto));
    return new ResponseEntity<>(gitLabGroupMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}/groups/registered/{groupId}")
  public HttpEntity<GitLabGroupDetailsDto> updateRootGroup(@PathVariable("id") Long integrationId,
      @PathVariable("groupId") Long groupId, @RequestBody @Valid GitLabGroupFormDto dto) {
    LOGGER.info("Updating GitLab root group for integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabGroup existing = gitLabIntegrationService.findRootGroupById(groupId)
        .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
    if (!existing.getGitGroup().getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested group belongs to another organization: " + groupId);
    }
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabGroup updated = gitLabIntegrationService.updateRootGroup(gitLabGroupMapper.fromFormDto(dto));
    return new ResponseEntity<>(gitLabGroupMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @PatchMapping("/{id}/groups/registered/{groupId}")
  public HttpEntity<?> updateRootGroupStatus(@PathVariable("id") Long integrationId,
      @PathVariable("groupId") Long groupId, @RequestParam("status") boolean status) {
    LOGGER.info("Updating GitLab root group for integration: {}", integrationId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabGroup existing = gitLabIntegrationService.findRootGroupById(groupId)
        .orElseThrow(() -> new IllegalArgumentException("Group not found: " + groupId));
    if (!existing.getGitGroup().getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested group belongs to another organization: " + groupId);
    }
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabGroup group = gitLabIntegrationService.findRootGroupById(groupId)
        .orElseThrow(() -> new IllegalStateException("Group not found."));
    if (!group.getGitLabIntegration().getId().equals(integration.getId())) {
      throw new IllegalArgumentException("Requested group is associated with another integration: " + integrationId);
    }
    gitLabIntegrationService.updateRootGroupStatus(groupId, status);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}/groups/registered/{groupId}")
  public HttpEntity<?> removeRootGroup(@PathVariable("id") Long integrationId,
      @PathVariable("groupId") Long groupId) {
    LOGGER.info("Removing GitLab root group: {}", groupId);
    Organization organization = organizationService.getCurrentOrganization();
    GitLabIntegration integration = gitLabIntegrationService.findById(integrationId)
        .orElseThrow(() -> new IllegalArgumentException("Integration not found: " + integrationId));
    if (!integration.getOrganization().getId().equals(organization.getId())) {
      throw new IllegalArgumentException("Requested integration belongs to another organization: " + integrationId);
    }
    GitLabGroup rootGroup = gitLabIntegrationService.findRootGroupById(groupId)
        .orElseThrow(() -> new IllegalStateException("Group not found."));
    if (!rootGroup.getGitLabIntegration().getId().equals(integration.getId())) {
      throw new IllegalArgumentException("Requested group is associated with another integration: " + integrationId);
    }
    gitLabIntegrationService.removeRootGroup(rootGroup);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
