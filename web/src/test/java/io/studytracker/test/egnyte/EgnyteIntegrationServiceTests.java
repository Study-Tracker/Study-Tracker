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

package io.studytracker.test.egnyte;

import io.studytracker.Application;
import io.studytracker.egnyte.EgnyteIntegrationService;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
import io.studytracker.repository.EgnyteDriveRepository;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.repository.StorageDriveRepository;
import io.studytracker.service.OrganizationService;
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
@ActiveProfiles({"egnyte-integration-test", "example"})
public class EgnyteIntegrationServiceTests {

  @Autowired private EgnyteIntegrationService egnyteIntegrationService;

  @Autowired private EgnyteIntegrationRepository egnyteIntegrationRepository;
  @Autowired private EgnyteDriveRepository egnyteDriveRepository;
  @Autowired private StorageDriveRepository storageDriveRepository;
  @Autowired private OrganizationService organizationService;
  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Value("${egnyte.test.tenant-name}")
  private String tenantName;

  @Value("${egnyte.test.api-token}")
  private String apiToken;

  @Value("${egnyte.test.root-url}")
  private String rootUrl;

  @Value("${egnyte.test.qps}")
  private int qps;

  @Before
  public void doBefore() throws Exception {
    exampleDataGenerator.populateDatabase();
    Organization organization = organizationService.getCurrentOrganization();
    System.out.println(organization.getName());
    egnyteDriveRepository.deleteAll();
    storageDriveRepository.deleteAll();
    egnyteIntegrationRepository.deleteAll();
  }

  @Test
  public void registerEgnyteIntegrationTest() throws Exception {

    Organization organization = organizationService.getCurrentOrganization();
    Assert.assertNotNull(organization);
    Assert.assertEquals(0, egnyteIntegrationRepository.count());
    Assert.assertEquals(0, egnyteDriveRepository.count());
    Assert.assertEquals(0, storageDriveRepository.count());

    EgnyteIntegration integration = new EgnyteIntegration();
    integration.setTenantName(tenantName);
    integration.setRootUrl(rootUrl);
    integration.setApiToken(apiToken);
    integration.setQps(qps);
    integration.setOrganization(organization);

    EgnyteIntegration created = egnyteIntegrationService.register(integration);
    Assert.assertNotNull(created.getId());
    Assert.assertEquals(1, egnyteIntegrationRepository.count());

  }

}
