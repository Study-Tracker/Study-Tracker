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

package io.studytracker.config.initialization;

import io.studytracker.model.AssayType;
import io.studytracker.service.AssayTypeService;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssayTypeInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AssayTypeInitializer.class);

  @Autowired private AssayTypeService assayTypeService;

  @PostConstruct
  public void initializeAdminUser() {
    if (assayTypeService.count() > 0) {
      return;
    }
    LOGGER.info("No assay types defined. Initializing base assay types...");
    AssayType assayType = new AssayType();
    assayType.setName("Generic");
    assayType.setDescription("Generic assay type");
    assayType.setActive(true);
    assayTypeService.create(assayType);
  }
}
