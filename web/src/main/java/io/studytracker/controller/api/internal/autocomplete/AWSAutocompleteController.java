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

package io.studytracker.controller.api.internal.autocomplete;

import io.studytracker.aws.AwsIntegrationService;
import io.studytracker.model.AwsIntegration;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/autocomplete/aws")
public class AWSAutocompleteController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AWSAutocompleteController.class);

  @Autowired private AwsIntegrationService awsIntegrationService;

  @GetMapping("/s3")
  public List<String> listAvailableBuckets(@RequestParam("q") String keyword) {
    LOGGER.debug("Listing available buckets with keyword: {}", keyword);
    AwsIntegration integration = awsIntegrationService.findAll().get(0);
    return awsIntegrationService.listAvailableBuckets(integration)
        .stream().filter(bucket -> !StringUtils.hasText(keyword)
            || bucket.toLowerCase().contains(keyword.toLowerCase()))
        .collect(Collectors.toList());
  }

}
