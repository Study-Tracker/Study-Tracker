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

package io.studytracker.config.initialization;

import io.studytracker.config.properties.StudyTrackerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates records for external service integrations defined in the application.properties file.
 *
 * @author Will Oemler
 * @since 0.7.1
 */
@Component
public class IntegrationInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationInitializer.class);

  @Autowired private StudyTrackerProperties properties;
  @Autowired private LocalStorageInitializer localStorageInitializer;
  @Autowired private EgnyteIntegrationInitializer egnyteIntegrationInitializer;
  @Autowired private AwsIntegrationInitializer awsIntegrationInitializer;
  @Autowired private GitLabIntegrationInitializer gitLabIntegrationInitializer;
  @Autowired private BenchlingIntegrationInitializer benchlingIntegrationInitializer;

  public void run() throws Exception {

    LOGGER.info("Initializing integrations...");

    LOGGER.info("Initializing local storage...");
    localStorageInitializer.initializeIntegrations();

    LOGGER.info("Initializing Egnyte integrations...");
    egnyteIntegrationInitializer.initializeIntegrations();

    LOGGER.info("Initializing AWS integrations...");
    awsIntegrationInitializer.initializeIntegrations();

    LOGGER.info("Initializing GitLab integrations...");
    gitLabIntegrationInitializer.initializeIntegrations();
    
    LOGGER.info("Initializing Benchling integrations...");
    benchlingIntegrationInitializer.initializeIntegrations();

    LOGGER.info("Integrations initialized.");
  }
}
