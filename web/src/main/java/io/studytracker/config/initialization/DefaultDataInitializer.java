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

import io.studytracker.config.ConfigOrder;
import io.studytracker.model.Organization;
import io.studytracker.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(ConfigOrder.DATA_INIT)
public class DefaultDataInitializer implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDataInitializer.class);

  @Autowired private AdminUserInitializer adminUserInitializer;
  @Autowired private AssayTypeInitializer assayTypeInitializer;
  @Autowired private DefaultOrganizationInitializer defaultOrganizationInitializer;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LOGGER.info("Initializing default data...");

    LOGGER.info("Initializing assay types...");
    assayTypeInitializer.initializeAssayTypes();

    LOGGER.info("Initializing admin user...");
    User admin = adminUserInitializer.initializeAdminUser();

    LOGGER.info("Initializing default organization...");
    Organization organization = defaultOrganizationInitializer.initializeDefaultOrganization();

    LOGGER.info("Default data initialized.");

  }
}
