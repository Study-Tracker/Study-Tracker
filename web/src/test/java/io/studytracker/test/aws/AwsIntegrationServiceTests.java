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

package io.studytracker.test.aws;


import io.studytracker.Application;
import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.service.OrganizationService;
import java.util.List;
import org.junit.Assert;
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
@ActiveProfiles({"aws-test"})
public class AwsIntegrationServiceTests {

  @Autowired private AwsIntegrationService awsIntegrationService;
  @Autowired private AwsIntegrationRepository awsIntegrationRepository;
  @Autowired private OrganizationService organizationService;

  @Value("${aws.access-key-id}")
  private String accessKeyId;

  @Value("${aws.secret-access-key}")
  private String secretAccessKey;

  @Value("${aws.region}")
  private String region;

  @Value("${aws.example-s3-bucket}")
  private String s3BucketName;

  @Test
  public void configTest() {
    Organization organization = organizationService.getCurrentOrganization();
    Assert.assertNotNull(organization);
    List<AwsIntegration> integrations = awsIntegrationService.findByOrganization(organization);
    Assert.assertNotNull(integrations);
    Assert.assertEquals(1, integrations.size());
    AwsIntegration integration = integrations.get(0);
    Assert.assertNotNull(integration);
    Assert.assertNotNull(integration.getAccessKeyId());
    Assert.assertNotNull(integration.getSecretAccessKey());
    Assert.assertFalse(integration.isUseIam());
  }

  @Test
  public void integrationValidationTest() throws Exception {
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = new AwsIntegration();
    integration.setAccessKeyId(accessKeyId);
    integration.setRegion(region);
    integration.setUseIam(false);
    integration.setOrganization(organization);
    integration.setName("Test Integration");
    integration.setAccountNumber("123456789012");
    Assert.assertFalse(awsIntegrationService.validate(integration));
    integration.setSecretAccessKey(secretAccessKey);
    Assert.assertTrue(awsIntegrationService.validate(integration));
  }

  @Test
  public void integrationTestTest() throws Exception {
    Organization organization = organizationService.getCurrentOrganization();
    AwsIntegration integration = new AwsIntegration();
    integration.setAccessKeyId(accessKeyId);
    integration.setRegion(region);
    integration.setUseIam(false);
    integration.setOrganization(organization);
    integration.setName("Test Integration");
    integration.setAccountNumber("123456789012");
    Assert.assertFalse(awsIntegrationService.test(integration));
    integration.setSecretAccessKey(secretAccessKey);
    Assert.assertTrue(awsIntegrationService.test(integration));
  }

}
