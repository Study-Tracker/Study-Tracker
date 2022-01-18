package com.decibeltx.studytracker.test.benchling;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.benchling.api.BenchlingElnRestClient;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingAuthenticationToken;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntry;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntrySchema;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntrySchemaList;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingFolder;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingProject;
import com.decibeltx.studytracker.benchling.api.entities.BenchlingProjectList;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.ssl.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"benchling-test", "example"})
public class BenchlingElnRestClientTests {

  @Autowired
  @Qualifier("benchlingElnRestTemplate")
  private RestTemplate restTemplate;

  @Value("${benchling.root-url}")
  private URL rootUrl;

  @Autowired
  private BenchlingElnRestClient client;

  @Autowired
  private Environment env;

  @Value("${benchling.api.username:}")
  private String username;

  @Value("${benchling.api.password:}")
  private String password;

  @Value("${benchling.api.client-id:}")
  private String clientId;

  @Value("${benchling.api.client-secret:}")
  private String clientSecret;

  @Value("${benchling.api.token:}")
  private String token;

  private String generateAuthorizationHeader() {
    if (StringUtils.hasText(token)) {
      return "Basic " + token;
    } else if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
      String auth = username + ":" + password;
      byte[] bytes = Base64.encodeBase64(auth.getBytes(StandardCharsets.US_ASCII));
      return "Basic " + new String(bytes);
    } else {
      String token = client.acquireApplicationAuthenticationToken(clientId, clientSecret)
          .getAccessToken();
      return "Bearer " + token;
    }
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
    ResponseEntity<BenchlingAuthenticationToken> response = restTemplate.exchange(
        url.toString(),
        HttpMethod.POST,
        request,
        BenchlingAuthenticationToken.class
    );
    Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    Assert.assertNotNull(response.getBody().getAccessToken());
  }

  @Test
  public void findProjectsTest() {
    String header = generateAuthorizationHeader();
    List<BenchlingProject> projects = new ArrayList<>();
    boolean hasNext = true;
    String nextToken = null;
    while (hasNext) {
      BenchlingProjectList projectList = client.findProjects(header, nextToken);
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
    String header = generateAuthorizationHeader();
    BenchlingProject project = client.findProjects(header, null).getProjects()
        .stream().findFirst().get();
    Optional<BenchlingProject> projectOptional = client.findProjectById(project.getId(), header);
    Assert.assertTrue(projectOptional.isPresent());
    BenchlingProject project2 = projectOptional.get();
    Assert.assertEquals(project.getName(), project2.getName());
  }

  @Test
  public void findRootFoldersTest() throws Exception {
    String header = generateAuthorizationHeader();
    List<BenchlingFolder> folders = client.findRootFolders(header, null).getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findProjectFolderChildrenTest() throws Exception {
    String header = generateAuthorizationHeader();
    BenchlingProject project = client.findProjects(header, null).getProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    List<BenchlingFolder> folders = client.findProjectFolderChildren(project.getId(), header, null)
        .getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderChildrenTest() throws Exception {
    String header = generateAuthorizationHeader();
    BenchlingProject project = client.findProjects(header, null).getProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    BenchlingFolder parentFolder = client.findRootFolders(header, null).getFolders().stream()
        .filter(f -> f.getProjectId().equals(project.getId()))
        .findFirst()
        .get();
    List<BenchlingFolder> folders = client.findFolderChildren(parentFolder.getId(), header, null)
        .getFolders();
    Assert.assertFalse(folders.isEmpty());
    for (BenchlingFolder folder : folders) {
      System.out.println(folder.toString());
    }
  }

  @Test
  public void findFolderByIdTest() throws Exception {
    String header = generateAuthorizationHeader();
    BenchlingProject project = client.findProjects(header, null).getProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    BenchlingFolder parentFolder = client.findRootFolders(header, null).getFolders().stream()
        .filter(f -> f.getProjectId().equals(project.getId()))
        .findFirst()
        .get();
    Optional<BenchlingFolder> folderOptional = client.findFolderById(parentFolder.getId(), header);
    Assert.assertTrue(folderOptional.isPresent());
    BenchlingFolder folder = folderOptional.get();
    Assert.assertEquals(parentFolder.getName(), folder.getName());
  }

  @Test
  public void findAllEntriesTest() throws Exception {
    String header = generateAuthorizationHeader();
    List<BenchlingEntry> entries = client.findAllEntries(header, null).getEntries();
    Assert.assertNotNull(entries);
    Assert.assertFalse(entries.isEmpty());
    for (BenchlingEntry entry: entries) {
      System.out.println(entry.toString());
    }
  }

  @Test
  public void findProjectEntriesTest() throws Exception {
    String header = generateAuthorizationHeader();
    BenchlingProject project = client.findProjects(header, null).getProjects().stream()
        .filter(p -> p.getName().equals(env.getRequiredProperty("notebook.test.default-project")))
        .findFirst()
        .get();
    List<BenchlingEntry> entries = client.findProjectEntries(project.getId(), header, null)
        .getEntries();
    Assert.assertNotNull(entries);
    Assert.assertFalse(entries.isEmpty());
    for (BenchlingEntry entry: entries) {
      System.out.println(entry.toString());
    }

  }

  @Test
  public void findAllEntryTemplatesTest() throws Exception {
    String header = generateAuthorizationHeader();
    List<BenchlingEntryTemplate> templates = client.findEntryTemplates(header, null)
        .getEntryTemplates();
    Assert.assertNotNull(templates);
    Assert.assertFalse(templates.isEmpty());
    for (BenchlingEntryTemplate template: templates) {
      System.out.println(template.toString());
    }
  }

  @Test
  public void findAllEntrySchemasTest() throws Exception {
    String header = generateAuthorizationHeader();
    BenchlingEntrySchemaList schemaList = client.findEntrySchemas(header, null);
    Assert.assertNotNull(schemaList);
    List<BenchlingEntrySchema> schemas = schemaList.getEntrySchemas();
    Assert.assertNotNull(schemas);
    Assert.assertFalse(schemas.isEmpty());
    for (BenchlingEntrySchema schema: schemas) {
      System.out.println(schema.toString());
    }
  }

}
