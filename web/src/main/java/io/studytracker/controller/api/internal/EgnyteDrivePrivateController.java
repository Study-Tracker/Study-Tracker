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

import io.studytracker.egnyte.EgnyteIntegrationService;
import io.studytracker.mapstruct.dto.response.EgnyteDriveDetailsDto;
import io.studytracker.mapstruct.mapper.EgnyteDriveMapper;
import io.studytracker.model.EgnyteDrive;
import io.studytracker.model.EgnyteIntegration;
import io.studytracker.model.Organization;
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
@RequestMapping("/api/internal/drives/egnyte")
public class EgnyteDrivePrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(EgnyteDrivePrivateController.class);

  @Autowired
  private EgnyteIntegrationService egnyteIntegrationService;

  @Autowired
  private EgnyteDriveMapper egnyteDriveMapper;

  @Autowired
  private OrganizationService organizationService;

  @GetMapping("")
  public List<EgnyteDriveDetailsDto> findAllDrives() {
    Organization organization = organizationService.getCurrentOrganization();
    LOGGER.debug("Finding all egnyte drives for organization: {}", organization.getId());
    List<EgnyteDrive> drives = new ArrayList<>();
    for (EgnyteIntegration integration: egnyteIntegrationService.findByOrganization(organization)) {
      drives.addAll(egnyteIntegrationService.listIntegrationDrives(integration));
    }
    return egnyteDriveMapper.toDetailsDto(drives);
  }

}
