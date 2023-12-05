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
import io.studytracker.repository.GitGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal/git-groups")
public class GitGroupPrivateController {

  public static final Logger LOGGER = LoggerFactory.getLogger(GitGroupPrivateController.class);

  @Autowired
  private GitGroupRepository gitGroupRepository;

  @Autowired
  private GitGroupMapper gitGroupMapper;

  @GetMapping
  public List<GitGroupDetailsDto> findAll(@RequestParam(name = "root", required = false) boolean isRoot) {
    LOGGER.debug("findAll(isRoot={})", isRoot);
    List<GitGroup> groups;
    if (isRoot) {
      groups = gitGroupRepository.findRoot();
    } else {
      groups = gitGroupRepository.findAll();
    }
    return gitGroupMapper.toDetailsDto(groups);
  }

  @GetMapping("/{id}")
  public GitGroupDetailsDto findById(@PathVariable("id") Long groupId) {
    LOGGER.debug("findById(groupId={})", groupId);
    GitGroup group = gitGroupRepository.findById(groupId)
        .orElseThrow(() -> new RecordNotFoundException("GitGroup not found with id: " + groupId));
    return gitGroupMapper.toDetailsDto(group);
  }

}
