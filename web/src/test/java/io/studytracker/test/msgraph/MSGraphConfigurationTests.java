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
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.Drive;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.requests.DriveCollectionPage;
import com.microsoft.graph.requests.DriveItemCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.SiteCollectionPage;
import io.studytracker.Application;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
@ActiveProfiles({"msgraph-test"})
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

  private GraphServiceClient buildCLient() {
    ClientSecretCredential credential = new ClientSecretCredentialBuilder()
        .clientId(clientId)
        .clientSecret(secret)
        .tenantId(tenantId)
        .build();

    TokenCredentialAuthProvider provider = new TokenCredentialAuthProvider(Arrays.asList(scope), credential);

    return GraphServiceClient
        .builder()
        .authenticationProvider(provider)
        .buildClient();
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

    TokenCredentialAuthProvider provider = new TokenCredentialAuthProvider(Arrays.asList(scope),
        credential);
    Assert.assertNotNull(provider);

    GraphServiceClient graphClient = GraphServiceClient
        .builder()
        .authenticationProvider(provider)
        .buildClient();
    Assert.assertNotNull(graphClient);

  }

  @Test
  public void findSharepointSitesTest() throws Exception {

    GraphServiceClient graphClient = buildCLient();

    // Get all sites
    List<Site> siteList = new ArrayList<>();
    SiteCollectionPage sitePage = graphClient.sites().buildRequest().get();
    while (sitePage != null) {
      siteList.addAll(sitePage.getCurrentPage());
      if (sitePage.getNextPage() == null) {
        break;
      } else {
        sitePage = sitePage.getNextPage().buildRequest().get();
      }
    }
    for (Site site : siteList) {
      System.out.println(
          "Site: " + site.displayName + "  ID: " + site.id + "  URL: " + site.webUrl);
    }

    // Get the Study Tracker site
    Site site = siteList.stream()
        .filter(s -> s.displayName != null && s.displayName.equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);
    Assert.assertEquals("Study Tracker Development", site.displayName);
    Assert.assertNotNull(site.id);
    System.out.println("Site: " + site.displayName
        + " \nID: " + site.id
        + " \nURL: " + site.webUrl
        + " \nName: " + site.name
        + " \nRoot: " + site.root.oDataType
        + " \nDescription: " + site.description
    );

  }

  @Test
  public void findSharepointSiteDrivesTest() throws Exception {

    GraphServiceClient graphClient = buildCLient();
    SiteCollectionPage sitePage = graphClient.sites().buildRequest().get();
    Site site = sitePage.getCurrentPage().stream()
        .filter(s -> s.displayName != null && s.displayName.equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);

    // Get the Study Tracker site drives
    DriveCollectionPage page = graphClient.sites(site.id).drives().buildRequest().get();
    Assert.assertNotNull(page);
    System.out.println(page);
    System.out.println("# Drives: " + page.getCount());
    for (Drive drive:  page.getCurrentPage()) {
      System.out.println("ID: " + drive.id + "  Name: " + drive.name + "  URL: " + drive.webUrl);
    }
    Drive drive = page.getCurrentPage().get(0);
    Assert.assertNotNull(drive);

    // Get the root folder

    Drive stDrive = graphClient.drives(drive.id).buildRequest().get();
    Assert.assertNotNull(stDrive);

    DriveItem driveRootFolder = graphClient.drives(drive.id).root().itemWithPath("/").buildRequest().get();
    Assert.assertNotNull(driveRootFolder);
    System.out.println("Root folder...");
    System.out.println("Name: " + driveRootFolder.name + "  ID: " + driveRootFolder.id + "  URL: " + driveRootFolder.webUrl);
    System.out.println("Path: " + driveRootFolder.parentReference.path + "  ID: " + driveRootFolder.parentReference.id + "  Has Parent: " + (driveRootFolder.parentReference != null ? "True":"False"));

    driveRootFolder = graphClient.drives(drive.id).root().itemWithPath("/Project A").buildRequest().get();
    Assert.assertNotNull(driveRootFolder);
    System.out.println("Project A folder...");
    System.out.println("Name: " + driveRootFolder.name + "  ID: " + driveRootFolder.id + "  URL: " + driveRootFolder.webUrl);
    System.out.println("Path: " + driveRootFolder.parentReference.path + "  ID: " + driveRootFolder.parentReference.id);

    Exception exception = null;
    DriveItem badFolder = null;
    try {
      badFolder = graphClient.drives(drive.id).root().itemWithPath("/badpath")
          .buildRequest().get();
    } catch (Exception e) {
      exception = e;
    }
    Assert.assertNotNull(exception);
    Assert.assertTrue(exception instanceof GraphServiceException);
    Assert.assertNull(badFolder);

    // Get the root folder contents

    List<DriveItem> driveItems = new ArrayList<>();
    DriveItemCollectionPage itemPage = graphClient.drives(drive.id).items(driveRootFolder.id).children().buildRequest().get();
    while (itemPage != null) {
      driveItems.addAll(itemPage.getCurrentPage());
      if (itemPage.getNextPage() == null) {
        break;
      } else {
        itemPage = itemPage.getNextPage().buildRequest().get();
      }
    }
    Assert.assertFalse(driveItems.isEmpty());
    for (DriveItem item: driveItems) {
      System.out.println("ID: " + item.id
          + "  Name: " + item.name
          + "  URL: " + item.webUrl
          + "  Last Modified: " + new Date(item.lastModifiedDateTime.toInstant().toEpochMilli()));
    }

  }

  @Test
  public void uploadFileTest() throws Exception {

    GraphServiceClient graphClient = buildCLient();

    // Get the Study Tracker site
    SiteCollectionPage sitePage = graphClient.sites().buildRequest().get();
    Site site = sitePage.getCurrentPage().stream()
        .filter(s -> s.displayName != null && s.displayName.equals("Study Tracker Development"))
        .findFirst()
        .orElse(null);
    Assert.assertNotNull(site);

    // Get the Study Tracker site drives
    DriveCollectionPage drivePage = graphClient.sites(site.id).drives().buildRequest().get();
    Drive drive = drivePage.getCurrentPage().get(0);
    Assert.assertNotNull(drive);

    // Get the root folder

    Drive stDrive = graphClient.drives(drive.id).buildRequest().get();
    Assert.assertNotNull(stDrive);

    DriveItem driveRootFolder = graphClient.drives(drive.id).root().itemWithPath("/").buildRequest().get();
    Assert.assertNotNull(driveRootFolder);

    // Upload a file

    Resource resource = new ClassPathResource("test.txt");
    Assert.assertNotNull(resource);
    File file = resource.getFile();
    Assert.assertNotNull(file);
    Assert.assertTrue(file.exists());
    DriveItem item = graphClient.drives(drive.id).items(driveRootFolder.id).children("another-test.txt")
        .content()
        .buildRequest()
        .put(resource.getInputStream().readAllBytes());
    Assert.assertNotNull(item);
    Assert.assertEquals("another-test.txt", item.name);

    // Download the file
    InputStream inputStream = graphClient.drives(drive.id).items(item.id).content()
        .buildRequest().get();
    Assert.assertNotNull(inputStream);
    File downloadedFile = new File("/tmp/downloaded-test.txt");
    FileUtils.copyInputStreamToFile(inputStream, downloadedFile);
    Assert.assertTrue(downloadedFile.exists());
    Assert.assertTrue(downloadedFile.length() > 0);

  }

}
