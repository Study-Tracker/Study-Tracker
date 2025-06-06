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

package io.studytracker.msgraph;

import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.DriveCollectionResponse;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.models.SiteCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.OneDriveDriveDetails;
import io.studytracker.model.SharePointSite;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.SharePointSiteRepository;
import io.studytracker.repository.StorageDriveRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class MSGraphIntegrationService implements IntegrationService<MSGraphIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MSGraphIntegrationService.class);

  @Autowired
  private MSGraphIntegrationRepository integrationRepository;

  @Autowired
  private SharePointSiteRepository sharePointSiteRepository;

  @Autowired
  private StorageDriveRepository storageDriveRepository;

  @Override
  public Optional<MSGraphIntegration> findById(Long id) {
    LOGGER.debug("Finding MSGraphIntegration by id: {}", id);
    return integrationRepository.findById(id);
  }

  @Override
  public List<MSGraphIntegration> findAll() {
    LOGGER.debug("Finding all MSGraphIntegrations");
    return integrationRepository.findAll();
  }

  @Transactional
  @Override
  public MSGraphIntegration register(MSGraphIntegration instance) {
    LOGGER.info("Registering MSGraphIntegration");
    if (!validate(instance)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(instance)) {
      throw new IllegalArgumentException("Failed to connect to Graph API with the provided credentials.");
    }
    instance.setActive(true);
    return integrationRepository.save(instance);
  }

  @Transactional
  @Override
  public MSGraphIntegration update(MSGraphIntegration instance) {
    LOGGER.info("Updating MSGraphIntegration: {}", instance.getId());
    if (!validate(instance)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(instance)) {
      throw new IllegalArgumentException("Failed to connect to Graph API with the provided credentials.");
    }
    MSGraphIntegration i = integrationRepository.getById(instance.getId());
    i.setTenantId(instance.getTenantId());
    i.setClientId(instance.getClientId());
    i.setClientSecret(instance.getClientSecret());
    i.setActive(instance.isActive());
    return integrationRepository.save(i);
  }

  @Override
  public boolean validate(MSGraphIntegration instance) {
    try {
      Assert.isTrue(StringUtils.hasText(instance.getClientId()), "Client ID is required");
      Assert.isTrue(StringUtils.hasText(instance.getClientSecret()), "Client Secret is required");
      Assert.isTrue(StringUtils.hasText(instance.getTenantId()), "Tenant ID is required");
    } catch (Exception e) {
      LOGGER.error("MSGraphIntegration validation failed: {}", e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  public boolean test(MSGraphIntegration instance) {
    LOGGER.info("Testing MSGraphIntegration: {}", instance.getTenantId());
    try {
      GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(instance);
      DriveCollectionResponse page = client.drives().get();
      return page != null;
    } catch (Exception e) {
      LOGGER.error("MSGraphIntegration test failed: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public void remove(MSGraphIntegration instance) {
    LOGGER.info("Removing MSGraphIntegration: {}", instance.getId());
    MSGraphIntegration i = integrationRepository.getById(instance.getId());
    i.setActive(false);
    integrationRepository.save(i);
  }

  // SharePoint

  public List<SharePointSite> listRegisteredSharepointSites(MSGraphIntegration integration) {
    LOGGER.debug("Listing registered SharePoint sites");
    return sharePointSiteRepository.findByIntegrationId(integration.getId());
  }

  public List<SharePointSite> listAvailableSharepointSites(MSGraphIntegration integration) {
    LOGGER.debug("Listing available SharePoint sites");
    GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(integration);
    SiteCollectionResponse page = client.sites().get();
    return page.getValue().stream()
        .filter(s -> !s.getIsPersonalSite())
        .map(s -> SharePointUtils.fromSite(s))
        .toList();
  }

  public SharePointSite findSharepointSiteBySiteId(MSGraphIntegration integration, String siteId) {
    LOGGER.debug("Finding SharePoint site by site id: {}", siteId);
    GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(integration);
    Site site = null;
    try {
      site = client.sites().bySiteId(siteId).get();
    } catch (Exception e) {
      LOGGER.error("Failed to find SharePoint site by site id: {}", siteId);
    }
    if (site != null) {
      return SharePointUtils.fromSite(site);
    } else {
      return null;
    }
  }

  public Optional<SharePointSite> findSharePointSiteById(Long id) {
    LOGGER.debug("Finding SharePoint site by id: {}", id);
    return sharePointSiteRepository.findById(id);
  }

  @Transactional
  public SharePointSite registerSharePointSite(SharePointSite site) {
    LOGGER.info("Registering SharePoint site: {}", site.getSiteId());
    List<MSGraphIntegration> integrations = findAll();
    MSGraphIntegration integration = integrations.iterator().next();
    GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(integration);
    Site s = client.sites().bySiteId(site.getSiteId()).get();
    if (s == null) {
      throw new IllegalArgumentException("SharePoint site not found");
    }
    site.setMsgraphIntegration(integration);
    site.setUrl(s.getWebUrl());
    if (!StringUtils.hasText(site.getName())) site.setName(s.getName());
    site.setSiteId(s.getId());
    site.setActive(true);
    return sharePointSiteRepository.save(site);
  }

  @Transactional
  public SharePointSite updateSharePointSite(SharePointSite site) {
    LOGGER.info("Updating SharePoint site: {}", site.getId());
    SharePointSite s = sharePointSiteRepository.getById(site.getId());
    s.setActive(site.isActive());
    return sharePointSiteRepository.save(s);
  }

  @Transactional
  public void deleteSharePointSite(SharePointSite site) {
    LOGGER.info("Deleting SharePoint site: {}", site.getId());
//    SharePointSite s = sharePointSiteRepository.getById(site.getId());
//    s.setActive(false);
//    sharePointSiteRepository.save(s);
    sharePointSiteRepository.deleteById(site.getId());
  }

  // Drives

  public Optional<StorageDrive> findRegisteredDriveById(MSGraphIntegration integration, Long id) {
    LOGGER.debug("Finding registered OneDrive drive by id: {}", id);
    return this.listRegisteredDrives(integration).stream()
        .filter(d -> d.getId().equals(id))
        .findFirst();
  };

  public List<StorageDrive> listRegisteredDrives(MSGraphIntegration integration) {
    LOGGER.debug("Listing registered OneDrive drives for integration: " + integration.getId());
    return storageDriveRepository.findByDriveType(DriveType.ONEDRIVE)
        .stream()
        .filter(drive -> drive.getDetails() instanceof OneDriveDriveDetails
            && ((OneDriveDriveDetails) drive.getDetails()).getMsGraphIntegrationId().equals(integration.getId()))
        .collect(Collectors.toList());
  }

  @Transactional
  public List<StorageDrive> registerSharePointDrives(SharePointSite site) {

    LOGGER.info("Registering OneDrive drives for site: {}", site.getSiteId());

    // Get the client
    MSGraphIntegration integration = findAll().get(0);
    GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(integration);

    // Make sure the site exists
    Site s = client.sites().bySiteId(site.getSiteId()).get();
    if (s == null || s.getId() == null) {
      throw new IllegalArgumentException("SharePoint site not found");
    }

    // get existing site drives
    List<String> existingDrives = storageDriveRepository.findByDriveType(DriveType.ONEDRIVE)
        .stream()
        .filter(drive -> drive.getDetails() instanceof OneDriveDriveDetails
            && ((OneDriveDriveDetails) drive.getDetails()).getMsGraphIntegrationId().equals(integration.getId()))
        .map(d -> ((OneDriveDriveDetails) d.getDetails()).getDriveId())
        .collect(Collectors.toList());

    // Get the drives
    DriveCollectionResponse page = client.sites().bySiteId(s.getId()).drives().get();

    // Register the drives
    List<StorageDrive> storageDrives = new ArrayList<>();
    for (Drive drive: page.getValue()) {

      if (existingDrives.contains(drive.getId())) {
        LOGGER.info("Drive already registered: {}", drive.getId());
        continue; // Skip already registered drives
      }

      StorageDrive storageDrive = new StorageDrive();
      storageDrive.setDriveType(DriveType.ONEDRIVE);
      storageDrive.setRootPath("/");
      storageDrive.setDisplayName("SharePoint Site " + site.getName() + " Drive: "
          + drive.getName());
      storageDrive.setActive(true);

      OneDriveDriveDetails details = new OneDriveDriveDetails();
      details.setDriveId(drive.getId());
      details.setName(drive.getName());
      details.setMsGraphIntegrationId(integration.getId());
      details.setWebUrl(drive.getWebUrl());
      storageDrive.setDetails(details);

      storageDrives.add(storageDrive);

    }

    return storageDriveRepository.saveAll(storageDrives);

  }

  @Transactional
  public StorageDrive registerOneDriveDrive(OneDriveDriveDetails drive) {

    LOGGER.info("Registering OneDrive drive: {}", drive.getDriveId());

    // Get the client
    MSGraphIntegration integration = findAll().get(0);
    GraphServiceClient client = MSGraphClientFactory.fromIntegrationInstance(integration);

    // Make sure the drive exists
    Drive d = client.drives().byDriveId(drive.getDriveId()).get();
    if (d == null) {
      throw new IllegalArgumentException("OneDrive drive not found: " + drive.getDriveId());
    }

    // Register the drive
    StorageDrive storageDrive = new StorageDrive();
    storageDrive.setDriveType(DriveType.ONEDRIVE);
    storageDrive.setRootPath("/");
    storageDrive.setDisplayName("OneDrive Drive: " + d.getName());
    storageDrive.setActive(true);

    OneDriveDriveDetails oneDriveDriveDetails = new OneDriveDriveDetails();
    oneDriveDriveDetails.setDriveId(d.getId());
    oneDriveDriveDetails.setName(d.getName());
    oneDriveDriveDetails.setMsGraphIntegrationId(integration.getId());
    oneDriveDriveDetails.setWebUrl(d.getWebUrl());
    storageDrive.setDetails(oneDriveDriveDetails);

    return storageDriveRepository.save(storageDrive);

  }

  @Transactional
  public void deleteOneDriveDrive(StorageDrive drive) {
    LOGGER.info("Removing OneDrive drive: {}", drive.getId());

    // Make sure the drive is unused
    List<Long> unusedDrives = storageDriveRepository.findUnusedDrives()
        .stream()
        .filter(d -> d.getDriveType() == DriveType.ONEDRIVE)
        .map(StorageDrive::getId)
        .toList();
    LOGGER.debug("Unused drives: {}", unusedDrives);
    if (!unusedDrives.contains(drive.getId())) {
      throw new IllegalArgumentException("Drive is in use and cannot be removed: " + drive.getId());
    }
    storageDriveRepository.deleteById(drive.getId());
  }

  @Transactional
  public void deleteAllUnusedDrives() {
    LOGGER.info("Removing all unused OneDrive drives");
    Set<StorageDrive> unusedDrives = storageDriveRepository.findUnusedDrives()
        .stream()
        .filter(d -> d.getDriveType() == DriveType.ONEDRIVE)
        .collect(Collectors.toSet());
    for (StorageDrive drive : unusedDrives) {
      storageDriveRepository.deleteById(drive.getId());
    }
  }

}
