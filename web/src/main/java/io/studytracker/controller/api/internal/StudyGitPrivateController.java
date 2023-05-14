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

import io.studytracker.controller.api.AbstractStudyController;
import io.studytracker.exception.InvalidRequestException;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.response.GitRepositoryDetailsDto;
import io.studytracker.mapstruct.mapper.GitRepositoryMapper;
import io.studytracker.model.GitGroup;
import io.studytracker.model.GitRepository;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.service.GitRepositoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/study/{studyId}/git")
public class StudyGitPrivateController extends AbstractStudyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StudyGitPrivateController.class);

  @Autowired private GitRepositoryService gitRepositoryService;

  @Autowired private GitRepositoryMapper gitRepositoryMapper;

  @GetMapping("")
  public List<GitRepositoryDetailsDto> listStudyGitRepositories(@PathVariable("studyId") String studyId)
      throws Exception {
    LOGGER.info("Creating Git repository for study {}", studyId);
    Study study = this.getStudyFromIdentifier(studyId);
    Optional<GitRepository> optional = gitRepositoryService.findStudyGitRepository(study);
    List<GitRepository> repositories = new ArrayList<>();
    optional.ifPresent(repositories::add);
    return gitRepositoryMapper.toDetailsDto(repositories);
  }

  @PostMapping("")
  public GitRepositoryDetailsDto createStudyGitRepository(@PathVariable("studyId") String studyId) {
    LOGGER.info("Creating Git repository for study {}", studyId);
    Study study = this.getStudyFromIdentifier(studyId);
    Program program = this.getProgramService().findById(study.getProgram().getId())
        .orElseThrow(() -> new RecordNotFoundException("Program not found: " + study.getProgram().getId()));
    GitGroup programGroup = gitRepositoryService.findProgramGitGroup(program)
        .orElseThrow(() -> new InvalidRequestException("No Git group found for program: " + program.getId()));
    try {
      GitRepository gitRepository = this.getStudyService().addGitRepository(study, programGroup);
      return gitRepositoryMapper.toDetailsDto(gitRepository);
    } catch (Exception e) {
      throw new InvalidRequestException("Failed to create Git repository for study: " + studyId, e);
    }
  }

}
