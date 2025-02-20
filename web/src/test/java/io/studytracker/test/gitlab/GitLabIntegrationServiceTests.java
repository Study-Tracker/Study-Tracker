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
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.git.GitServerGroup;
import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitLabIntegration;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.repository.GitLabGroupRepository;
import io.studytracker.repository.GitLabIntegrationRepository;
import java.util.List;
import java.util.Optional;
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
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabIntegrationServiceTests {

  private static final String EXAMPLE_GROUP = "API Client Test Group";

  @Autowired
  private GitLabIntegrationService integrationService;

  @Autowired
  private GitLabIntegrationRepository integrationRepository;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private ExampleDataRunner exampleDataRunner;

  @Autowired
  private GitLabService gitLabService;;

  @Value("${gitlab.access-key}")
  private String accessKey;

  @Value("${gitlab.url}")
  private String rootUrl;

  @Value("${gitlab.root-group-id}")
  private Integer rootGroupId;

  @Before
  public void setup() {
    exampleDataRunner.populateDatabase();
    gitLabGroupRepository.deleteAll();
    gitGroupRepository.deleteAll();
    integrationRepository.deleteAll();
  }

  @Test
  public void registerIntegrationTest() throws Exception {

    Assert.assertEquals(0, integrationRepository.count());
    Assert.assertEquals(0, gitGroupRepository.count());
    Assert.assertEquals(0, gitLabGroupRepository.count());

    List<GitLabIntegration> integrations = integrationService.findAll();
    Assert.assertTrue(integrations.isEmpty());
    Assert.assertEquals(0, integrationRepository.count());

    GitLabIntegration integration = new GitLabIntegration();
    Assert.assertFalse(integrationService.validate(integration));
    Assert.assertFalse(integrationService.test(integration));

    integration.setRootUrl(rootUrl);
    integration.setAccessToken(accessKey);
    integration.setName("GitLab Integration Test");
    integration.setActive(true);
    Assert.assertTrue(integrationService.validate(integration));
    Assert.assertTrue(integrationService.test(integration));

    integration = integrationService.register(integration);
    Assert.assertNotNull(integration.getId());
    Assert.assertEquals("GitLab Integration Test", integration.getName());
    integration.setName("GitLab Integration Test 2");
    integration = integrationService.update(integration);
    Assert.assertEquals("GitLab Integration Test 2", integration.getName());

    Assert.assertEquals(1, integrationRepository.count());
    Assert.assertEquals(0, gitGroupRepository.count());
    Assert.assertEquals(0, gitLabGroupRepository.count());

  }

  @Test
  public void registerDefaultGroupTest() throws Exception {

    registerIntegrationTest();

    Assert.assertEquals(0, gitGroupRepository.count());
    Assert.assertEquals(0, gitLabGroupRepository.count());

    List<GitLabIntegration> integrations = integrationService.findAll();
    Assert.assertEquals(1, integrations.size());
    GitLabIntegration integration = integrations.get(0);

    List<GitServerGroup> groups = gitLabService.listAvailableGroups(integration);
    Assert.assertFalse(groups.isEmpty());
    Optional<GitServerGroup> optional = groups.stream()
        .filter(g -> g.getName().equals(EXAMPLE_GROUP))
        .findFirst();
    Assert.assertTrue(optional.isPresent());
    GitServerGroup serverGroup = optional.get();
    Assert.assertEquals(EXAMPLE_GROUP, serverGroup.getName());

    GitGroup rootGroup = gitLabService.registerGroup(integration, serverGroup);
    Assert.assertNotNull(rootGroup);
    Assert.assertEquals(1, gitGroupRepository.count());
    Assert.assertEquals(1, gitLabGroupRepository.count());

  }

}
