/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.test.benchling;

import io.studytracker.Application;
import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.*;
import io.studytracker.model.BenchlingIntegration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"benchling-test", "example"})
public class BenchlingElnRestClientTests {

  @Autowired
  @Qualifier("benchlingElnRestTemplate")
  private RestTemplate restTemplate;
  
  @Value("${benchling.tenant-name}")
  private String tenantName;

  @Value("${benchling.root-url}")
  private URL rootUrl;

  @Autowired private Environment env;

  @Value("${benchling.api.username:}")
  private String username;

  @Value("${benchling.api.password:}")
  private String password;

  @Value("${benchling.api.client-id:}")
  private String clientId;

  @Value("${benchling.api.client-secret:}")
  private String clientSecret;

  private BenchlingElnRestClient client;
  
  @BeforeAll
  public void init() {
    BenchlingIntegration integration = new BenchlingIntegration();
    integration.setClientId(clientId);
    integration.setClientSecret(clientSecret);
    integration.setName("Benchling");
    integration.setTenantName(tenantName);
    integration.setRootUrl(rootUrl.toString());
    client = new BenchlingElnRestClient(restTemplate, integration);
  }

  @Test
  public void generateAuthTokenTest() throws Exception {
    URL url = new URL(rootUrl, "api/v2/token");
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/json");
    headers.set("Content-Type", "application/x-www-form-urlencoded");
    MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
    data.add("client_id", clientId);
    data.add("client_secret", clientSecret);
    data.add("grant_type", "client_credentials");
    HttpEntity<?> request = new HttpEntity<>(data, headers);
    ResponseEntity<BenchlingAuthenticationToken> response =
        restTemplate.exchange(
            url.toString(), HttpMethod.POST, request, BenchlingAuthenticationToken.class);
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertNotNull(response.getBody().getAccessToken());
  }

  @Test
  public void findProjectsTest() {
    List<BenchlingProject> projects = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingProjectList projectList = client.findProjects(nextToken);
      projects.addAll(projectList.getProjects());
      nextToken = projectList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    Assert.assertNotNull(projects);
    Assert.assertFalse(projects.isEmpty());
    for (BenchlingProject project : projects) {
      System.out.println(project.toString());
    }
  }

  @Test
  public void findProjectByIdTest() throws Exception {
    BenchlingProject project = client.findProjects(null).getProjects().stream().findFirst().get();
    Optional<BenchlingProject> projectOptional = client.findProjectById(project.getId());
    Assert.assertTrue(projectOptional.isPresent());
    BenchlingProject project2 = projectOptional.get();
    Assert.assertEquals(project.getName(), project2.getName());
  }

  @Test
  public void findRootFoldersTest() throws Exception {
    List<BenchlingFolder> folders = client.findRootFolders(null).getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findProjectFolderChildrenTest() throws Exception {
    BenchlingProject project = client.findProjects(null).getProjects().stream()
            .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
            .findFirst()
            .get();
    List<BenchlingFolder> folders =
        client.findProjectFolderChildren(project.getId(), null).getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderChildrenTest() throws Exception {
    BenchlingProject project = client.findProjects(null).getProjects().stream()
            .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
            .findFirst()
            .get();
    BenchlingFolder parentFolder =
        client.findRootFolders(null).getFolders().stream()
            .filter(f -> f.getProjectId().equals(project.getId()))
            .findFirst()
            .get();
    List<BenchlingFolder> folders = client.findFolderChildren(parentFolder.getId(), null).getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderByIdTest() throws Exception {
    BenchlingProject project = client.findProjects(null).getProjects().stream()
            .filter(
                p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
            .findFirst()
            .get();
    BenchlingFolder parentFolder = client.findRootFolders(null).getFolders().stream()
            .filter(f -> f.getProjectId().equals(project.getId()))
            .findFirst()
            .get();
    Optional<BenchlingFolder> folderOptional = client.findFolderById(parentFolder.getId());
    Assert.assertTrue(folderOptional.isPresent());
    BenchlingFolder folder = folderOptional.get();
    Assert.assertEquals(parentFolder.getName(), folder.getName());
  }

  @Test
  public void findAllEntriesTest() throws Exception {
    List<BenchlingEntry> entries = client.findAllEntries(null).getEntries();
    Assert.assertNotNull(entries);
    Assert.assertFalse(entries.isEmpty());
    for (BenchlingEntry entry : entries) {
      System.out.println(entry.toString());
    }
  }

  @Test
  public void findProjectEntriesTest() throws Exception {
    BenchlingProject project =
        client.findProjects(null).getProjects().stream()
            .filter(
                p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
            .findFirst()
            .get();
    List<BenchlingEntry> entries =
        client.findProjectEntries(project.getId(), null).getEntries();
    Assert.assertNotNull(entries);
    Assert.assertFalse(entries.isEmpty());
    for (BenchlingEntry entry : entries) {
      System.out.println(entry.toString());
    }
  }

  @Test
  public void findAllEntryTemplatesTest() throws Exception {
    List<BenchlingEntryTemplate> templates =
        client.findEntryTemplates(null).getEntryTemplates();
    Assert.assertNotNull(templates);
    Assert.assertFalse(templates.isEmpty());
    for (BenchlingEntryTemplate template : templates) {
      System.out.println(template.toString());
    }
  }

  @Test
  public void findAllEntrySchemasTest() throws Exception {
    BenchlingEntrySchemaList schemaList = client.findEntrySchemas(null);
    Assert.assertNotNull(schemaList);
    List<BenchlingEntrySchema> schemas = schemaList.getEntrySchemas();
    Assert.assertNotNull(schemas);
    Assert.assertFalse(schemas.isEmpty());
    for (BenchlingEntrySchema schema : schemas) {
      System.out.println(schema.toString());
    }
  }

  @Test
  public void uriEncodingTest() throws Exception {
    LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.set(
        "nextToken",
        "W3siX19iZW5jaGxpbmdUeXBlIjogImRhdGV0aW1lIiwgInZhbHVlIjogIjIwMjEtMDctMjRUMDM6MTc6MTUuNjk4NjE0In0sICJlbnRfZE9ZQW4wdXQiXQ==");
    URL url = new URL(rootUrl, "/api/internal/v2/users");
    UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.fromHttpUrl(url.toString()).queryParams(params);
    UriComponents components = uriComponentsBuilder.build().encode();
    String out = components.toString();
    System.out.println(out);
  }
}
