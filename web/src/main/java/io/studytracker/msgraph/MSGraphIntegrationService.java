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
import com.microsoft.graph.models.Site;
import com.microsoft.graph.requests.DriveCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.SiteCollectionPage;
import io.studytracker.integration.IntegrationService;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.OneDriveDriveDetails;
import io.studytracker.model.Organization;
import io.studytracker.model.SharePointSite;
import io.studytracker.model.StorageDrive;
import io.studytracker.model.StorageDrive.DriveType;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.SharePointSiteRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class MSGraphIntegrationService implements IntegrationService<MSGraphIntegration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(MSGraphIntegrationService.class);

  @Autowired
  private MSGraphIntegrationRepository integrationRepository;

  @Autowired
  private OrganizationService organizationService;

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
  public List<MSGraphIntegration> findByOrganization(Organization organization) {
    LOGGER.debug("Finding MSGraphIntegration by organization: {}", organization.getId());
    return integrationRepository.findByOrganizationId(organization.getId());
  }

  @Override
  public MSGraphIntegration register(MSGraphIntegration instance) {
    LOGGER.info("Registering MSGraphIntegration for organization: {}",
        instance.getOrganization().getId());
    Organization organization = organizationService.getCurrentOrganization();
    instance.setOrganization(organization);
    if (!validate(instance)) {
      throw new IllegalArgumentException("One or more required fields are missing.");
    }
    if (!test(instance)) {
      throw new IllegalArgumentException("Failed to connect to Graph API with the provided credentials.");
    }
    instance.setActive(true);
    return integrationRepository.save(instance);
  }

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
      GraphServiceClient<?> client = MSGraphClientFactory.fromIntegrationInstance(instance);
      SiteCollectionPage page = client.sites().buildRequest().get();
      if (page != null && page.getCurrentPage().size() > 0) {
        LOGGER.info("MSGraphIntegration test successful");
        return true;
      } else {
        LOGGER.error("MSGraphIntegration test failed: No sites found");
        return false;
      }
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
    List<SharePointSite> sites = new ArrayList<>();
    GraphServiceClient<?> client = MSGraphClientFactory.fromIntegrationInstance(integration);
    SiteCollectionPage page = client.sites().buildRequest().get();
    while (page != null) {
      page.getCurrentPage().forEach(site -> {
        if (site.webUrl != null && site.displayName != null && site.id != null && !site.webUrl.contains("/personal/")) {
          sites.add(SharePointUtils.fromSite(site));
        }
      });
      if (page.getNextPage() != null) {
        page = page.getNextPage().buildRequest().get();
      } else {
        page = null;
      }
    }
    return sites;
  }

  public Optional<SharePointSite> findSharePointSiteById(Long id) {
    LOGGER.debug("Finding SharePoint site by id: {}", id);
    return sharePointSiteRepository.findById(id);
  }

  public SharePointSite registerSharePointSite(SharePointSite site) {
    LOGGER.info("Registering SharePoint site: {}", site.getSiteId());
    Organization organization = organizationService.getCurrentOrganization();
    List<MSGraphIntegration> integrations = findByOrganization(organization);
    MSGraphIntegration integration = integrations.iterator().next();
    GraphServiceClient<?> client = MSGraphClientFactory.fromIntegrationInstance(integration);
    Site s = client.sites(site.getSiteId()).buildRequest().get();
    if (s == null) {
      throw new IllegalArgumentException("SharePoint site not found");
    }
    site.setMsgraphIntegration(integration);
    site.setUrl(s.webUrl);
    if (!StringUtils.hasText(site.getName())) site.setName(s.name);
    site.setSiteId(s.id);
    site.setActive(true);
    return sharePointSiteRepository.save(site);
  }

  public SharePointSite updateSharePointSite(SharePointSite site) {
    LOGGER.info("Updating SharePoint site: {}", site.getId());
    SharePointSite s = sharePointSiteRepository.getById(site.getId());
    s.setActive(site.isActive());
    return sharePointSiteRepository.save(s);
  }

  public List<StorageDrive> listRegisteredDrives(MSGraphIntegration integration) {
    LOGGER.debug("Listing registered OneDrive drives for integration: " + integration.getId());
    Organization organization = organizationService.getCurrentOrganization();
    return storageDriveRepository.findByOrganizationAndDriveType(organization.getId(), DriveType.ONEDRIVE)
        .stream()
        .filter(drive -> drive.getDetails() instanceof OneDriveDriveDetails
            && ((OneDriveDriveDetails) drive.getDetails()).getMsGraphIntegrationId().equals(integration.getId()))
        .collect(Collectors.toList());
  }

  public List<StorageDrive> registerSharePointDrives(SharePointSite site) {

    LOGGER.info("Registering OneDrive drives for site: {}", site.getSiteId());

    // Get the client
    Organization organization = organizationService.getCurrentOrganization();
    MSGraphIntegration integration = findByOrganization(organization).get(0);
    GraphServiceClient<?> client = MSGraphClientFactory.fromIntegrationInstance(integration);

    // Make sure the site exists
    Site s = client.sites(site.getSiteId()).buildRequest().get();
    if (s == null) {
      throw new IllegalArgumentException("SharePoint site not found");
    }

    // Get the drives
    List<Drive> drives = new ArrayList<>();
    DriveCollectionPage page = client.sites(s.id).drives().buildRequest().get();
    while (page != null) {
      page.getCurrentPage().forEach(drive -> {
        if (drive.webUrl != null && drive.name != null && drive.id != null) {
          drives.add(drive);
        }
      });
      if (page.getNextPage() != null) {
        page = page.getNextPage().buildRequest().get();
      } else {
        page = null;
      }
    }

    // Register the drives
    List<StorageDrive> storageDrives = new ArrayList<>();
    for (Drive drive: drives) {

      StorageDrive storageDrive = new StorageDrive();
      storageDrive.setDriveType(DriveType.ONEDRIVE);
      storageDrive.setRootPath("/");
      storageDrive.setOrganization(organization);
      storageDrive.setDisplayName("SharePoint Site Drive: " + site.getName());
      storageDrive.setActive(true);

      OneDriveDriveDetails details = new OneDriveDriveDetails();
      details.setDriveId(drive.id);
      details.setName(drive.name);
      details.setMsGraphIntegrationId(integration.getId());
      details.setWebUrl(drive.webUrl);
      storageDrive.setDetails(details);

      storageDrives.add(storageDrive);

    }

    return storageDrives;

  }

  public StorageDrive registerOneDriveDrive(OneDriveDriveDetails drive) {

    LOGGER.info("Registering OneDrive drive: {}", drive.getDriveId());

    // Get the client
    Organization organization = organizationService.getCurrentOrganization();
    MSGraphIntegration integration = findByOrganization(organization).get(0);
    GraphServiceClient<?> client = MSGraphClientFactory.fromIntegrationInstance(integration);

    // Make sure the drive exists
    Drive d = client.drives(drive.getDriveId()).buildRequest().get();
    if (d == null) {
      throw new IllegalArgumentException("OneDrive drive not found: " + drive.getDriveId());
    }

    // Register the drive
    StorageDrive storageDrive = new StorageDrive();
    storageDrive.setDriveType(DriveType.ONEDRIVE);
    storageDrive.setRootPath("/");
    storageDrive.setOrganization(organization);
    storageDrive.setDisplayName("OneDrive Drive: " + d.name);
    storageDrive.setActive(true);

    OneDriveDriveDetails oneDriveDriveDetails = new OneDriveDriveDetails();
    oneDriveDriveDetails.setDriveId(d.id);
    oneDriveDriveDetails.setName(d.name);
    oneDriveDriveDetails.setMsGraphIntegrationId(integration.getId());
    oneDriveDriveDetails.setWebUrl(d.webUrl);
    storageDrive.setDetails(oneDriveDriveDetails);

    return storageDriveRepository.save(storageDrive);

  }

}
