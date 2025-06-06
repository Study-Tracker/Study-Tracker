/*
 * Copyright 2019-2025 the original author or authors.
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

import io.studytracker.config.ConfigOrder;
import io.studytracker.config.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(ConfigOrder.DATA_INIT+1)
public class IntegrationInitializationRunner implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(IntegrationInitializationRunner.class);

  @Autowired private IntegrationInitializer integrationInitializer;
  @Autowired private ApplicationProperties properties;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (properties.isSkipInitialization()) {
      LOGGER.info("Skipping integration initialization.");
    } else {
      integrationInitializer.run();
    }
  }
}
