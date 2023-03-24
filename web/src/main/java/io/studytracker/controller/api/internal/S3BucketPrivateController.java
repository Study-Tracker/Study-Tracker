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

package io.studytracker.controller.api.internal;

import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.mapstruct.dto.response.S3BucketDetailsDto;
import io.studytracker.mapstruct.mapper.S3BucketMapper;
import io.studytracker.model.AwsIntegration;
import io.studytracker.model.Organization;
import io.studytracker.model.S3Bucket;
import io.studytracker.service.OrganizationService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/s3-buckets")
public class S3BucketPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(S3BucketPrivateController.class);

  @Autowired
  private AwsIntegrationService awsIntegrationService;

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private S3BucketMapper mapper;

  @GetMapping("")
  public List<S3BucketDetailsDto> findAllBuckets() {
    LOGGER.debug("Finding all S3 buckets");
    Organization organization = organizationService.getCurrentOrganization();
    List<AwsIntegration> integrations = awsIntegrationService.findByOrganization(organization);
    if (integrations.isEmpty()) {
      LOGGER.debug("No AWS integrations found for organization: {}", organization.getId());
      return new ArrayList<>();
    }
    AwsIntegration awsIntegration = integrations.get(0);
    List<S3Bucket> buckets = awsIntegrationService.findRegisteredBuckets(awsIntegration);
    return mapper.toDto(buckets);
  }

}
