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

package io.studytracker.controller.api.internal;

import io.studytracker.mapstruct.dto.features.AuthFeaturesDto;
import io.studytracker.mapstruct.dto.features.FeaturesSummaryDto;
import io.studytracker.mapstruct.dto.features.NotebookFeaturesDto;
import io.studytracker.mapstruct.dto.features.SearchFeaturesDto;
import io.studytracker.mapstruct.dto.features.StorageFeaturesDto;
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

  @GetMapping("/features")
  public FeaturesSummaryDto getFeatures() {

    LOGGER.info("Getting features");
    FeaturesSummaryDto features = new FeaturesSummaryDto();

    // Storage
    StorageFeaturesDto storageFeaturesDto = new StorageFeaturesDto();
    storageFeaturesDto.setMode(env.getProperty("storage.mode", "local"));
    features.setStorage(storageFeaturesDto);

    // ELN
    NotebookFeaturesDto notebookFeaturesDto = new NotebookFeaturesDto();
    notebookFeaturesDto.setMode(env.getProperty("notebook.mode", "none"));
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

    return features;

  }


}
