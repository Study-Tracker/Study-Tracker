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

package io.studytracker.test.gitlab;

import io.studytracker.Application;
import io.studytracker.gitlab.GitLabOptions;
import io.studytracker.gitlab.GitLabRestClient;
import io.studytracker.gitlab.GitLabUtils;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.gitlab.entities.GitLabNewProjectRequest;
import io.studytracker.gitlab.entities.GitLabProject;
import io.studytracker.gitlab.entities.GitLabUser;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabRestClientTests {

  private static final String EXAMPLE_GROUP = "API Client Test Group";

  @Autowired
  private GitLabRestClient client;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private GitLabOptions options;

  @Test
  public void findUsersTest() throws Exception {

    String token = options.getAccessToken();

    List<GitLabUser> users = client.findUsers(token);
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertTrue(users.size() > 1);

    users = client.findUsers(token, "oemler");
    Assert.assertNotNull(users);
    Assert.assertFalse(users.isEmpty());
    Assert.assertEquals(users.size(), 1);

    GitLabUser user = users.get(0);
    System.out.println(user.toString());

    Optional<GitLabUser> optional = client.findUserById(token, user.getId());
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals(optional.get().getName(), user.getName());

  }

  @Test
  public void findGroupsTest() {

    String token = options.getAccessToken();
    List<GitLabGroup> groups = client.findGroups(token);
    Assert.assertNotNull(groups);
    Assert.assertFalse(groups.isEmpty());
    Assert.assertTrue(groups.size() > 1);

    GitLabGroup group = groups.get(0);
    Optional<GitLabGroup> optional = client.findGroupById(token, group.getId());
    Assert.assertTrue(optional.isPresent());
    Assert.assertEquals(optional.get().getName(), group.getName());

  }

  @Test
  public void createGroupTest() {

    String token = options.getAccessToken();
    GitLabNewGroupRequest request = new GitLabNewGroupRequest();
    request.setName(EXAMPLE_GROUP);
    request.setPath(GitLabUtils.getPathFromName(EXAMPLE_GROUP));
    request.setDescription("Test group created by API client");
    request.setAutoDevOpsEnabled(false);
    request.setParentId(options.getRootGroupId());
    request.setVisibility("private");
    GitLabGroup group = client.createNewGroup(token, request);
    Assert.assertNotNull(group);
    Assert.assertEquals(group.getName(), EXAMPLE_GROUP);
    Assert.assertEquals(group.getPath(), GitLabUtils.getPathFromName(EXAMPLE_GROUP));
    Assert.assertEquals(group.getDescription(), "Test group created by API client");
    Assert.assertEquals(group.getParentId(), options.getRootGroupId());

  }

  @Test
  public void createProjectTest() {
    String token = options.getAccessToken();
    GitLabNewProjectRequest request = new GitLabNewProjectRequest();
    request.setName("API Client Test Project");
    request.setPath(GitLabUtils.getPathFromName("API Client Test Project"));
    request.setNamespaceId(options.getRootGroupId());
    request.setAutoDevopsEnabled(false);
    request.setDescription("Test project created by API client");
    request.setInitializeWithReadme(false);
    GitLabProject project = client.createProject(token, request);
    Assert.assertNotNull(project);
    Assert.assertNotNull(project.getId());
    Assert.assertEquals(project.getName(), "API Client Test Project");
    Assert.assertEquals(project.getPath(), GitLabUtils.getPathFromName("API Client Test Project"));
  }

}
