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

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.DriveCollectionResponse;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCollectionResponse;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.models.SiteCollectionResponse;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import io.studytracker.Application;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"msgraph-test", "example"})
public class MSGraphConfigurationTests {

  @Value("${ms-graph.authority}")
  private String authority;

  @Value("${ms-graph.tenant-id}")
  private String tenantId;

  @Value("${ms-graph.client-id}")
  private String clientId;

  @Value("${ms-graph.secret}")
  private String secret;

  @Value("${ms-graph.scope}")
  private String scope;

  private GraphServiceClient buildClient() {
    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .clientSecret(secret)
        .tenantId(tenantId)
        .build();
    return new GraphServiceClient(credential, scope);
  }

  @Test
  public void msalClientConfigTest() throws Exception {
    ConfidentialClientApplication app = ConfidentialClientApplication.builder(
            clientId,
            ClientCredentialFactory.createFromSecret(secret))
        .authority(authority)
        .build();
    Assert.assertNotNull(app);

    ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
            Collections.singleton(scope))
        .build();

    CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
    IAuthenticationResult result = future.get();
    Assert.assertNotNull(result);
    System.out.println("Access token: " + result.accessToken());

  }

  @Test
  public void graphClientConfigTest() throws Exception {

    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .clientSecret(secret)
        .tenantId(tenantId)
        .build();
    Assert.assertNotNull(credential);

    GraphServiceClient graphClient = new GraphServiceClient(credential, scope);
    Assert.assertNotNull(graphClient);

  }

  @Test
  public void findSharepointSitesTest() throws Exception {

    GraphServiceClient graphClient = buildClient();

    // Get all sites
    SiteCollectionResponse sitePage = graphClient.sites().get();
    List<Site> siteList = sitePage.getValue();

    for (Site site : siteList) {
      System.out.println(
          "Site: " + site.getDisplayName() + "  ID: " + site.getId() + "  URL: " + site.getWebUrl());
    }

    // Get the Study Tracker site
    Site site = siteList.stream()
        .filter(s -> s.getDisplayName() != null && s.getDisplayName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);
    Assert.assertEquals("Study Tracker Development", site.getDisplayName());
    Assert.assertNotNull(site.getId());
    System.out.println("Site: " + site.getDisplayName()
        + " \nID: " + site.getId()
        + " \nURL: " + site.getWebUrl()
        + " \nName: " + site.getName()
        + " \nRoot: " + site.getRoot().getOdataType()
        + " \nDescription: " + site.getDescription()
    );

  }

  @Test
  public void findSharepointSiteDrivesTest() throws Exception {

    GraphServiceClient graphClient = buildClient();
    SiteCollectionResponse sitePage = graphClient.sites().get();
    Site site = sitePage.getValue().stream()
        .filter(s -> s.getDisplayName() != null
            && s.getDisplayName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);

    // Get the Study Tracker site drives
    DriveCollectionResponse page = graphClient.sites().bySiteId(site.getId()).drives().get();
    Assert.assertNotNull(page);
    System.out.println(page);
    System.out.println("# Drives: " + page.getValue().size());
    for (Drive drive:  page.getValue()) {
      System.out.println("ID: " + drive.getId() + "  Name: " + drive.getName()
          + "  URL: " + drive.getWebUrl());
    }
    Drive drive = page.getValue().get(0);
    Assert.assertNotNull(drive);

    // Get the root folder

    Drive stDrive = graphClient.drives().byDriveId(drive.getId()).get();
    Assert.assertNotNull(stDrive);

    DriveItem driveRootFolder = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId("root:/").get();
    Assert.assertNotNull(driveRootFolder);
    System.out.println("Root folder...");
    System.out.println("Name: " + driveRootFolder.getName() + "  ID: " + driveRootFolder.getId()
        + "  URL: " + driveRootFolder.getWebUrl());
    System.out.println("Path: " + driveRootFolder.getParentReference().getPath()
        + "  ID: " + driveRootFolder.getParentReference().getId()
        + "  Has Parent: " + (driveRootFolder.getParentReference() != null ? "True":"False"));

    driveRootFolder = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId("root:/Project A").get();
    Assert.assertNotNull(driveRootFolder);
    System.out.println("Project A folder...");
    System.out.println("Name: " + driveRootFolder.getName() + "  ID: " + driveRootFolder.getId()
        + "  URL: " + driveRootFolder.getWebUrl());
    System.out.println("Path: " + driveRootFolder.getParentReference().getPath()
        + "  ID: " + driveRootFolder.getParentReference().getId());

    Exception exception = null;
    DriveItem badFolder = null;
    try {
      badFolder = graphClient.drives().byDriveId(drive.getId())
          .items().byDriveItemId("root:/badpath").get();
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertNull(badFolder);

    // Get the root folder contents

    DriveItemCollectionResponse itemPage = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId(driveRootFolder.getId()).children().get();
    List<DriveItem> driveItems = itemPage.getValue();
    Assert.assertFalse(driveItems.isEmpty());
    for (DriveItem item: driveItems) {
      System.out.println("ID: " + item.getId()
          + "  Name: " + item.getName()
          + "  URL: " + item.getWebUrl()
          + "  Last Modified: " + new Date(item.getLastModifiedDateTime().toInstant().toEpochMilli()));
    }

  }

  @Test
  public void uploadFileTest() throws Exception {

    GraphServiceClient graphClient = buildClient();

    // Get the Study Tracker site
    SiteCollectionResponse sitePage = graphClient.sites().get();
    Site site = sitePage.getValue().stream()
        .filter(s -> s.getDisplayName() != null
            && s.getDisplayName().equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);

    // Get the Study Tracker site drives
    DriveCollectionResponse drivePage = graphClient.sites().bySiteId(site.getId())
        .drives().get();
    Drive drive = drivePage.getValue().get(0);
    Assert.assertNotNull(drive);

    // Get the root folder

    Drive stDrive = graphClient.drives().byDriveId(drive.getId()).get();
    Assert.assertNotNull(stDrive);

    DriveItem driveRootFolder = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId("root:/").get();
    Assert.assertNotNull(driveRootFolder);

    // Upload a file

    Resource resource = new ClassPathResource("test.txt");
    Assert.assertNotNull(resource);
    File file = resource.getFile();
    Assert.assertNotNull(file);
    Assert.assertTrue(file.exists());
    DriveItem item = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId(driveRootFolder.getId()).children().byDriveItemId1("another-test.txt")
        .content()
        .put(resource.getInputStream());
    Assert.assertNotNull(item);
    Assert.assertEquals("another-test.txt", item.getName());

    // Download the file
    InputStream inputStream = graphClient.drives().byDriveId(drive.getId())
        .items().byDriveItemId(item.getId()).content().get();
    Assert.assertNotNull(inputStream);
    File downloadedFile = new File("/tmp/downloaded-test.txt");
    FileUtils.copyInputStreamToFile(inputStream, downloadedFile);
    Assert.assertTrue(downloadedFile.exists());
    Assert.assertTrue(downloadedFile.length() > 0);

  }

}
