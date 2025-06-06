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

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.form.MSGraphIntegrationFormDto;
import io.studytracker.mapstruct.dto.form.SharePointSiteFormDto;
import io.studytracker.mapstruct.dto.response.MSGraphIntegrationDetailsDto;
import io.studytracker.mapstruct.dto.response.SharePointSiteDetailsDto;
import io.studytracker.mapstruct.dto.response.StorageDriveDetailsDto;
import io.studytracker.mapstruct.mapper.MSGraphIntegrationMapper;
import io.studytracker.mapstruct.mapper.SharePointSiteMapper;
import io.studytracker.mapstruct.mapper.StorageDriveMapper;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.SharePointSite;
import io.studytracker.model.StorageDrive;
import io.studytracker.msgraph.MSGraphIntegrationService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/integrations/msgraph")
public class MSGraphIntegrationPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MSGraphIntegrationPrivateController.class);

  @Autowired
  private MSGraphIntegrationService msGraphIntegrationService;

  @Autowired
  private MSGraphIntegrationMapper msGraphIntegrationMapper;

  @Autowired
  private SharePointSiteMapper sharePointSiteMapper;

  @Autowired
  private StorageDriveMapper storageDriveMapper;

  @GetMapping("")
  public List<MSGraphIntegrationDetailsDto> fetchIntegrations() {
    LOGGER.debug("Fetching MS Graph integrations");
    return msGraphIntegrationMapper.toDetailsDto(msGraphIntegrationService.findAll());
  }

  @PostMapping("")
  public HttpEntity<MSGraphIntegrationDetailsDto> registerIntegration(@Valid @RequestBody MSGraphIntegrationFormDto dto) {
    LOGGER.info("Registering MS Graph integration");
    MSGraphIntegration integration = msGraphIntegrationMapper.fromFormDto(dto);
    MSGraphIntegration created = msGraphIntegrationService.register(integration);
    return new ResponseEntity<>(msGraphIntegrationMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public HttpEntity<MSGraphIntegrationDetailsDto> updateRegistration(@PathVariable("id") Long id,
      @Valid @RequestBody MSGraphIntegrationFormDto dto) {
    MSGraphIntegration integration = msGraphIntegrationMapper.fromFormDto(dto);
    LOGGER.info("Updating MS Graph integration {}", id);
    MSGraphIntegration updated = msGraphIntegrationService.update(integration);
    return new ResponseEntity<>(msGraphIntegrationMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<?> deleteIntegration(@PathVariable("id") Long id) {
    LOGGER.info("Deleting MS Graph integration {}", id);
    Optional<MSGraphIntegration> optional = msGraphIntegrationService.findById(id);
    if (optional.isEmpty()) {
      throw new RecordNotFoundException("MS Graph integration not found");
    }
    msGraphIntegrationService.remove(optional.get());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // Sharepoint

  @GetMapping("/{id}/sharepoint/available")
  public List<SharePointSiteDetailsDto> getAvailableSharepointSites(
      @PathVariable("id") Long integrationId,
      @RequestParam(value = "q", required = false) String query
  ) {
    LOGGER.debug("Fetching available Sharepoint sites for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    List<SharePointSite> sites = new ArrayList<>();
    sites.addAll(msGraphIntegrationService.listAvailableSharepointSites(integration));
    sites.addAll(msGraphIntegrationService.listRegisteredSharepointSites(integration));
    if (sites.isEmpty()) {
      SharePointSite site = msGraphIntegrationService
          .findSharepointSiteBySiteId(integration, query);
      if (site != null) sites.add(site);
    }

    return sites.stream()
        .collect(Collectors.toMap(SharePointSite::getSiteId, site -> site,
            (s1, s2) -> s1)) // remove duplicates
        .values()
        .stream()
        .filter(s -> StringUtils.hasText(s.getName()))
        .filter(s -> {
          if (StringUtils.hasText(query)) {
            return s.getName().toLowerCase().contains(query.toLowerCase());
          } else {
            return true;
          }
        })
        .map(sharePointSiteMapper::toDetailsDto)
        .toList();
  }

  @GetMapping("/{id}/sharepoint/sites")
  public List<SharePointSiteDetailsDto> getRegisteredSharepointSites(
      @PathVariable("id") Long integrationId) {
    LOGGER.debug("Fetching registered Sharepoint sites for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    List<SharePointSite> sites = msGraphIntegrationService.listRegisteredSharepointSites(integration);
    return sharePointSiteMapper.toDetailsDto(sites);
  }

  @PostMapping("/{id}/sharepoint/sites")
  public HttpEntity<SharePointSiteDetailsDto> registerSharePointSite(
      @PathVariable("id") Long integrationId, @Valid @RequestBody SharePointSiteFormDto dto) {
    LOGGER.info("Registering Sharepoint site for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    SharePointSite site = sharePointSiteMapper.fromFormDto(dto);
    site.setMsgraphIntegration(integration);

    // See if the site is registered already
    SharePointSite created = msGraphIntegrationService
        .listRegisteredSharepointSites(integration)
        .stream()
        .filter(s -> s.getSiteId().equalsIgnoreCase(site.getSiteId()))
        .findFirst()
        .orElse(null);
    if (created == null) {
      created = msGraphIntegrationService.registerSharePointSite(site);
    } else {
      LOGGER.info("Using existing Sharepoint site registration with ID {}", created.getId());
    }

    // Register available drives
    msGraphIntegrationService.registerSharePointDrives(created);
    return new ResponseEntity<>(sharePointSiteMapper.toDetailsDto(created), HttpStatus.CREATED);
  }

  @PutMapping("/{id}/sharepoint/sites/{siteId}")
  public HttpEntity<SharePointSiteDetailsDto> updateSharePointSiteRegistration(
      @PathVariable("id") Long integrationId,
      @PathVariable("siteId") Long siteId,
      @Valid @RequestBody SharePointSiteFormDto dto
  ) {
    LOGGER.info("Updating Sharepoint site registration for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    msGraphIntegrationService.findSharePointSiteById(siteId)
        .orElseThrow(() -> new RecordNotFoundException("Sharepoint site not found"));
    SharePointSite updated = msGraphIntegrationService.updateSharePointSite(
        sharePointSiteMapper.fromFormDto(dto));
    return new ResponseEntity<>(sharePointSiteMapper.toDetailsDto(updated), HttpStatus.OK);
  }

  @PostMapping("/{id}/sharepoint/sites/{siteId}/refresh")
  public HttpEntity<?> updateSharePointSiteRegistration(
      @PathVariable("id") Long integrationId, @PathVariable("siteId") Long siteId) {
    LOGGER.info("Refreshing Sharepoint site registration for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    SharePointSite site = msGraphIntegrationService.findSharePointSiteById(siteId)
        .orElseThrow(() -> new RecordNotFoundException("Sharepoint site not found"));
    msGraphIntegrationService.registerSharePointDrives(site);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}/sharepoint/sites/{siteId}")
  public HttpEntity<?> deleteSharePointSiteRegistration(
      @PathVariable("id") Long integrationId, @PathVariable("siteId") Long siteId) {
    LOGGER.info("Deleting Sharepoint site registration {}", siteId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    SharePointSite site = msGraphIntegrationService.findSharePointSiteById(siteId)
        .orElseThrow(() -> new RecordNotFoundException("Sharepoint site not found"));
    msGraphIntegrationService.deleteSharePointSite(site);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  // OneDrive

  @GetMapping("/{id}/onedrive/drives")
  public List<StorageDriveDetailsDto> findRegisteredOneDriveDrives(
      @PathVariable("id") Long integrationId) {
    LOGGER.debug("Fetching registered OneDrive drives for integration {}", integrationId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    List<StorageDrive> drives = msGraphIntegrationService.listRegisteredDrives(integration);
    return storageDriveMapper.toDetailsDto(drives);
  }

  @DeleteMapping("/{id}/onedrive/drives/{driveId}")
  public HttpEntity<?> deleteOneDriveDriveRegistration(
      @PathVariable("id") Long integrationId, @PathVariable("driveId") Long driveId) {
    LOGGER.info("Deleting OneDrive drive registration {}", driveId);
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    StorageDrive drive = msGraphIntegrationService.findRegisteredDriveById(integration, driveId)
        .orElseThrow(() -> new RecordNotFoundException("OneDrive drive not found"));
    msGraphIntegrationService.deleteOneDriveDrive(drive);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/{id}/onedrive/drives")
  public HttpEntity<?> deleteAllUsedOneDriveDriveRegistrations(
      @PathVariable("id") Long integrationId) {
    LOGGER.info("Deleting unused OneDrive drives");
    MSGraphIntegration integration = msGraphIntegrationService.findById(integrationId)
        .orElseThrow(() -> new RecordNotFoundException("MS Graph integration not found"));
    msGraphIntegrationService.deleteAllUnusedDrives();
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
