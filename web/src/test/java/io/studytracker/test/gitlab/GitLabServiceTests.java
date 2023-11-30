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

package io.studytracker.test.gitlab;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataRunner;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.gitlab.GitLabIntegrationService;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.gitlab.GitLabUtils;
import io.studytracker.model.*;
import io.studytracker.repository.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabServiceTests {

  private static final String EXAMPLE_PROGRAM = "Preclinical Project B";

  @Autowired private GitLabService gitLabService;

  @Autowired private ExampleDataRunner exampleDataRunner;

  @Autowired private ProgramRepository programRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private AssayRepository assayRepository;

  @Autowired private GitLabIntegrationService gitLabIntegrationService;

  @Autowired
  private GitLabGroupRepository gitLabGroupRepository;

  @Autowired
  private GitLabRepositoryRepository gitLabRepositoryRepository;

  @Before
  public void doBefore() {
    exampleDataRunner.populateDatabase();
  }

  @Test
  public void createProgramGroupTest() throws Exception {

    List<GitLabIntegration> integrations = gitLabIntegrationService.findAll();
    Assert.assertEquals(1, integrations.size());
    GitLabIntegration integration = integrations.get(0);

    Program program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(program.getGitGroups().isEmpty());
    List<GitGroup> rootGroups = gitLabService.findRegisteredGroups(integration, true);
    Assert.assertNotNull(rootGroups);
    Assert.assertEquals(1, rootGroups.size());
    GitGroup rootGroup = rootGroups.get(0);

    GitGroup group = gitLabService.createProgramGroup(rootGroup, program);
    Assert.assertNotNull(group);
    Assert.assertEquals(GitServiceType.GITLAB, group.getGitServiceType());
    Assert.assertEquals(program.getName() + " Program GitLab Project Group", group.getDisplayName());
    Assert.assertNotNull(group.getWebUrl());
    Assert.assertTrue(group.isActive());
    program.addGitGroup(group);
    programRepository.save(program);

    GitLabGroup gitLabGroup = gitLabGroupRepository.findByGitGroupId(group.getId());
    Assert.assertEquals(gitLabGroup.getName(), program.getName());
    Assert.assertEquals(gitLabGroup.getPath(), GitLabUtils.getPathFromName(program.getName()));
    program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(1, program.getGitGroups().size());

    GitGroup group2 = gitLabService.findProgramGroup(rootGroup, program).orElse(null);
    Assert.assertNotNull(group2);
    Assert.assertEquals(group.getId(), group2.getId());

  }

  @Test
  public void createStudyRepositoryTest() throws Exception {
    createProgramGroupTest();

    List<GitLabIntegration> integrations = gitLabIntegrationService.findAll();
    Assert.assertEquals(1, integrations.size());
    GitLabIntegration integration = integrations.get(0);
    List<GitGroup> rootGroups = gitLabService.findRegisteredGroups(integration, true);
    Assert.assertNotNull(rootGroups);
    Assert.assertEquals(1, rootGroups.size());
    GitGroup rootGroup = rootGroups.get(0);

    Program program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Study study = studyRepository.findByProgramId(program.getId()).get(0);
    Assert.assertNotNull(study);
    GitGroup programGroup = gitLabService.findProgramGroup(rootGroup, program).orElse(null);
    Assert.assertNotNull(programGroup);

    Exception exception = null;
    try {
      GitRepository repository = gitLabService.createStudyRepository(programGroup, study);
      Assert.assertNotNull(repository);
      Assert.assertEquals(GitLabUtils.getStudyProjectName(study), repository.getDisplayName());
      Assert.assertNotNull(repository.getHttpUrl());
      Assert.assertNotNull(repository.getSshUrl());
      Assert.assertNotNull(repository.getWebUrl());

      GitLabRepository gitLabRepository = gitLabRepositoryRepository.findByRepositoryId(
          repository.getId());
      Assert.assertEquals(gitLabRepository.getName(), GitLabUtils.getStudyProjectName(study));
      Assert.assertEquals(gitLabRepository.getPath(), GitLabUtils.getStudyProjectPath(study));
    } catch (Exception e) {
      exception = e;
      if (exception instanceof HttpClientErrorException) {
        HttpClientErrorException httpClientErrorException = (HttpClientErrorException) exception;
        Assert.assertEquals(400, httpClientErrorException.getRawStatusCode());
        Assert.assertTrue(httpClientErrorException.getResponseBodyAsString().contains("has already been taken"));
      }
    }

  }

//  @Test
//  public void createAssayRepository() throws Exception {
//    createProgramGroupTest();
//    Assay assay = assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
//    Assert.assertNotNull(assay);
//    GitServerRepository repository = gitLabService.createAssayRepository(assay);
//    Assert.assertNotNull(repository);
//    Assert.assertEquals(repository.getName(), GitLabUtils.getAssayProjectName(assay));
//    Assert.assertEquals(repository.getPath(), GitLabUtils.getAssayProjectPath(assay));
//
//    GitServerRepository repository2 = gitLabService.findAssayRepository(assay).orElseThrow(RecordNotFoundException::new);
//    Assert.assertNotNull(repository2);
//    Assert.assertEquals(repository.getRepositoryId(), repository2.getRepositoryId());
//  }

  @Test
  public void repositoryNamingTest() {

    String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    String trimmed = GitLabUtils.trimRepositoryDescription(description);
    System.out.println(trimmed);
    Assert.assertTrue(trimmed.length() < 255);

    Program program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Study study = studyRepository.findByProgramId(program.getId()).get(0);
    String projectName = GitLabUtils.getStudyProjectName(study);
    String projectPath = GitLabUtils.getStudyProjectPath(study);
    System.out.println(projectName);
    System.out.println(projectPath);
    Assert.assertTrue(projectName.length() < 255);
    Assert.assertTrue(projectPath.length() < 255);

  }

}
