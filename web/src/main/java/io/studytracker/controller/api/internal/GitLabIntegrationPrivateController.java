package io.studytracker.controller.api.internal;

import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.mapstruct.dto.form.GitLabIntegrationFormDto;
import io.studytracker.mapstruct.dto.response.GitLabIntegrationDetailsDto;
import io.studytracker.mapstruct.mapper.GitLabIntegrationMapper;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.model.Organization;
import io.studytracker.service.OrganizationService;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
