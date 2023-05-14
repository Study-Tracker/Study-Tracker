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

import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.GitGroupDetailsDto;
import io.studytracker.mapstruct.mapper.GitGroupMapper;
import io.studytracker.model.GitGroup;
import io.studytracker.model.Organization;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.service.OrganizationService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/git-groups")
public class GitGroupController {

  public static final Logger LOGGER = LoggerFactory.getLogger(GitGroupController.class);

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private GitGroupMapper gitGroupMapper;

  @GetMapping
  public List<GitGroupDetailsDto> findAll(@RequestParam(name = "root", required = false) boolean isRoot) {
    LOGGER.debug("findAll(isRoot={})", isRoot);
    Organization organization = organizationService.getCurrentOrganization();
    List<GitGroup> groups;
    if (isRoot) {
      groups = gitGroupRepository.findRootByOrganizationId(organization.getId());
    } else {
      groups = gitGroupRepository.findByOrganizationId(organization.getId());
    }
    return gitGroupMapper.toDetailsDto(groups);
  }

  @GetMapping("/{id}")
  public GitGroupDetailsDto findById(@PathVariable("id") Long groupId) {
    LOGGER.debug("findById(groupId={})", groupId);
    Organization organization = organizationService.getCurrentOrganization();
    GitGroup group = gitGroupRepository.findById(groupId)
        .orElseThrow(() -> new RecordNotFoundException("GitGroup not found with id: " + groupId));
    if (!group.getOrganization().getId().equals(organization.getId())) {
      throw new RecordNotFoundException("GitGroup not found with id: " + groupId);
    }
    return gitGroupMapper.toDetailsDto(group);
  }

}
