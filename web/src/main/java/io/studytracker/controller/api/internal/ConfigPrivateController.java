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

import io.studytracker.mapstruct.dto.features.*;
import io.studytracker.model.BenchlingIntegration;
import io.studytracker.repository.BenchlingIntegrationRepository;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Hidden
@RequestMapping("/api/internal/config")
public class ConfigPrivateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPrivateController.class);

  @Autowired private Environment env;
  @Autowired private BenchlingIntegrationRepository benchlingIntegrationRepository;

  @GetMapping("/features")
  public FeaturesSummaryDto getFeatures() {

    LOGGER.info("Getting features");
    FeaturesSummaryDto features = new FeaturesSummaryDto();

    // Storage
    StorageFeaturesDto storageFeaturesDto = new StorageFeaturesDto();
    String storageMode = env.getProperty("storage.mode", "local");
    storageFeaturesDto.setMode(storageMode);
    if (storageMode.equals("egnyte")) {
      storageFeaturesDto.setStorageServiceUrl(env.getProperty("egnyte.root-url"));
    }
    features.setStorage(storageFeaturesDto);

    // ELN
    BenchlingIntegration benchlingIntegration = benchlingIntegrationRepository.findAll().stream()
            .filter(i -> i.isActive())
            .findFirst()
            .orElse(null);
    NotebookFeaturesDto notebookFeaturesDto = new NotebookFeaturesDto();
    if (benchlingIntegration != null) {
      notebookFeaturesDto.setMode("benchling");
      notebookFeaturesDto.setElnUrl(benchlingIntegration.getRootUrl());
    } else {
      notebookFeaturesDto.setMode("none");
    }
    features.setNotebook(notebookFeaturesDto);

    // Search
    SearchFeaturesDto searchFeaturesDto = new SearchFeaturesDto();
    searchFeaturesDto.setMode(env.getProperty("search.mode", "none"));
    features.setSearch(searchFeaturesDto);

    // Authentication
    AuthFeaturesDto authFeaturesDto = new AuthFeaturesDto();
    String authMode = env.getProperty("security.sso", "none");
    authFeaturesDto.getSso().setMode(authMode);
    if (authMode.equals("okta-saml")) {
      authFeaturesDto.getSso().setSsoUrl(env.getRequiredProperty("sso.okta.url"));
    }
    features.setAuth(authFeaturesDto);

    // Git
    GitFeaturesDto gitFeaturesDto = new GitFeaturesDto();
    String gitMode = env.getProperty("git.mode", "none");
    gitFeaturesDto.setMode(gitMode);
    if (gitMode.equals("gitlab")) {
      gitFeaturesDto.setGitServerUrl(env.getRequiredProperty("gitlab.url"));
    }
    features.setGit(gitFeaturesDto);

    // AWS
    AWSFeaturesDto awsFeaturesDto = new AWSFeaturesDto();
    if (env.containsProperty("aws.region")) {
      awsFeaturesDto.setRegion(env.getRequiredProperty("aws.region"));
    }
//    if (env.containsProperty("aws.access-key-id")) {
//      awsFeaturesDto.setAccessKey(env.getRequiredProperty("aws.access-key-id"));
//    }
    if (env.containsProperty("aws.s3.default-study-location")) {
      awsFeaturesDto.setDefaultS3StudyLocation(
          env.getRequiredProperty("aws.s3.default-study-location"));
    }
    if (env.containsProperty("aws.eventbridge.bus-name")) {
      awsFeaturesDto.setEventBridgeBus(env.getRequiredProperty("aws.eventbridge.bus-name"));
    }
    features.setAws(awsFeaturesDto);

    return features;

  }


}
