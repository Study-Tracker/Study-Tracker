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

package io.studytracker.test.gitlab;

import io.studytracker.Application;
import io.studytracker.gitlab.GitLabRestClient;
import io.studytracker.gitlab.GitLabRestClient.GitLabRestClientBuilder;
import io.studytracker.gitlab.GitLabUtils;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabProjectGroup;
import io.studytracker.gitlab.entities.GitLabUser;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabRestClientTests {

  private static final String EXAMPLE_GROUP = "API Client Test Group";
  private static final String EXAMPLE_PROJECT = "API Client Test Project";

  @Value("${gitlab.access-key}")
  private String accessKey;

  @Value("${gitlab.url}")
  private String rootUrl;

  @Value("${gitlab.root-group-id}")
  private Integer rootGroupId;

  private GitLabRestClient client;
  private String groupName;
  private String projectName;

  @Before
  public void setup() {
    client = new GitLabRestClientBuilder()
        .accessToken(accessKey)
        .rootUrl(rootUrl)
        .build();
    String timestamp = "B"; //new Date().toString();
    groupName = EXAMPLE_GROUP + " " + timestamp;
    projectName = EXAMPLE_PROJECT + " " + timestamp;

  }

  @Test
  public void findUsersTest() throws Exception {

    List<GitLabUser> users = client.findUsers();
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertTrue(users.size() > 1);

    users = client.findUsers("oemler");
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertEquals(users.size(), 1);

    GitLabUser user = users.get(0);
    System.out.println(user.toString());

    Optional<GitLabUser> optional = client.findUserById(user.getId());
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals(optional.get().getName(), user.getName());

  }

  @Test
  public void findGroupsTest() {

    List<GitLabProjectGroup> groups = client.findGroups();
    Assert.assertNotNull(groups);
    Assert.assertFalse(groups.isEmpty());
    Assert.assertTrue(groups.size() > 1);

    GitLabProjectGroup group = groups.get(0);
    Optional<GitLabProjectGroup> optional = client.findGroupById(group.getId());
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals(optional.get().getName(), group.getName());

  }

  @Test
  public void createGroupTest() {

    GitLabNewGroupRequest request = new GitLabNewGroupRequest();
    request.setName(groupName);
    request.setPath(GitLabUtils.getPathFromName(groupName));
    request.setDescription("Test group created by API client");
    request.setAutoDevOpsEnabled(false);
    request.setParentId(rootGroupId);
    request.setVisibility("private");
    GitLabProjectGroup group = client.createNewGroup(request);
    Assert.assertNotNull(group);
    Assert.assertEquals(group.getName(), groupName);
    Assert.assertEquals(group.getPath(), GitLabUtils.getPathFromName(groupName));
    Assert.assertEquals(group.getDescription(), "Test group created by API client");
    Assert.assertEquals(group.getParentId(), rootGroupId);

  }

  @Test
  public void createProjectTest() {
    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setName(projectName);
    request.setPath(GitLabUtils.getPathFromName(projectName));
    request.setNamespaceId(rootGroupId);
    request.setAutoDevopsEnabled(false);
    request.setDescription("Test project created by API client");
    request.setInitializeWithReadme(false);
    GitLabProject project = client.createProject(request);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getId());
    Assert.assertEquals(project.getName(), projectName);
    Assert.assertEquals(project.getPath(), GitLabUtils.getPathFromName(projectName));
  }

}
