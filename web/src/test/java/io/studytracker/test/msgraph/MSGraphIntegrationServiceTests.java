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

package io.studytracker.test.msgraph;

import io.studytracker.Application;
import io.studytracker.model.MSGraphIntegration;
import io.studytracker.model.OneDriveDrive;
import io.studytracker.model.Organization;
import io.studytracker.model.SharePointSite;
import io.studytracker.msgraph.MSGraphIntegrationService;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.OneDriveDriveRepository;
import io.studytracker.repository.OneDriveFolderRepository;
import io.studytracker.repository.OrganizationRepository;
import io.studytracker.repository.SharePointSiteRepository;
import io.studytracker.repository.StorageDriveFolderRepository;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"msgraph-test"})
public class MSGraphIntegrationServiceTests {

  @Value("${ms-graph.tenant-id}")
  private String tenantId;

  @Value("${ms-graph.client-id}")
  private String clientId;

  @Value("${ms-graph.secret}")
  private String secret;

  @Autowired
  private MSGraphIntegrationService integrationService;

  @Autowired
  private MSGraphIntegrationRepository msGraphIntegrationRepository;

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private OneDriveDriveRepository oneDriveDriveRepository;

  @Autowired
  private SharePointSiteRepository sharePointSiteRepository;

  @Autowired
  private StorageDriveFolderRepository storageDriveFolderRepository;

  @Autowired
  private OneDriveFolderRepository oneDriveFolderRepository;

  @Before
  public void setup() {
    oneDriveFolderRepository.deleteAll();
    storageDriveFolderRepository.deleteAll();
    oneDriveDriveRepository.deleteAll();
    sharePointSiteRepository.deleteAll();
    msGraphIntegrationRepository.deleteAll();
  }

  @Test
  public void registerIntegrationTest() throws Exception {

    Organization organization = organizationRepository.findAll().get(0);
    Assert.assertNotNull(organization);

    MSGraphIntegration integration = new MSGraphIntegration();
    integration.setName("Azure");
    integration.setDomain("myorg.onmicrosoft.com");
    integration.setActive(true);
    integration.setOrganization(organization);
    integration.setTenantId(tenantId);
    integration.setClientId(clientId);
    integration.setClientSecret(secret);

    boolean isValid = integrationService.validate(integration);
    Assert.assertTrue(isValid);

    boolean isTested = integrationService.test(integration);
    Assert.assertTrue(isTested);

    Exception exception = null;
    MSGraphIntegration created = null;
    try {
      created = integrationService.register(integration);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(created);
    Assert.assertNotNull(integration.getId());

  }

  @Test
  public void badRegistrationTests() throws Exception {

    Organization organization = organizationRepository.findAll().get(0);
    Assert.assertNotNull(organization);

    MSGraphIntegration integration = new MSGraphIntegration();
    integration.setName("Azure");
    integration.setDomain("myorg.onmicrosoft.com");
    integration.setActive(true);
    integration.setOrganization(organization);
//    integration.setTenantId(tenantId);
    integration.setClientId(clientId);
    integration.setClientSecret(secret);

    boolean isValid = integrationService.validate(integration);
    Assert.assertFalse(isValid);

    boolean isTested = integrationService.test(integration);
    Assert.assertFalse(isTested);

    Exception exception = null;
    MSGraphIntegration created = null;
    try {
      created = integrationService.register(integration);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(created);

    integration.setTenantId(tenantId);
    isValid = integrationService.validate(integration);
    Assert.assertTrue(isValid);

    isTested = integrationService.test(integration);
    Assert.assertTrue(isTested);

    exception = null;
    created = null;
    try {
      created = integrationService.register(integration);
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(created);
    Assert.assertNotNull(integration.getId());

  }

  @Test
  public void findSharepointSitesTest() throws Exception {

    registerIntegrationTest();
    MSGraphIntegration integration = msGraphIntegrationRepository.findAll().get(0);
    Assert.assertNotNull(integration);

    List<SharePointSite> sites = integrationService.listAvailableSharepointSites(integration);
    Assert.assertNotNull(sites);
    Assert.assertFalse(sites.isEmpty());
    SharePointSite site = sites.stream()
        .filter(s -> s.getName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);

  }

  @Test
  public void registerSharePointSiteTest() throws Exception {

    registerIntegrationTest();
    MSGraphIntegration integration = msGraphIntegrationRepository.findAll().get(0);
    Assert.assertNotNull(integration);

    Assert.assertEquals(0, sharePointSiteRepository.count());
    Assert.assertEquals(0, oneDriveDriveRepository.count());

    SharePointSite site = integrationService.listAvailableSharepointSites(integration).stream()
        .filter(s -> s.getName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);

    SharePointSite created = integrationService.registerSharePointSite(site);
    Assert.assertNotNull(created);
    Assert.assertNotNull(created.getId());
    Assert.assertEquals(1, sharePointSiteRepository.count());
    Assert.assertEquals(0, oneDriveDriveRepository.count());

    List<OneDriveDrive> drives = integrationService.registerSharePointDrives(created);
    Assert.assertNotNull(drives);
    Assert.assertEquals(1, drives.size());
    OneDriveDrive drive = drives.get(0);
    System.out.println("Drive ID: " +  drive.getDriveId() + "  Name: " + drive.getName() + "  URL: " + drive.getWebUrl());

    Assert.assertEquals(1, sharePointSiteRepository.count());
    Assert.assertEquals(1, oneDriveDriveRepository.count());

  }


}
