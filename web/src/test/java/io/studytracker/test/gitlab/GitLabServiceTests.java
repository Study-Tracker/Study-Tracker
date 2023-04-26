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
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.git.GitAttributes;
import io.studytracker.git.GitServerGroup;
import io.studytracker.git.GitServerRepository;
import io.studytracker.gitlab.GitLabService;
import io.studytracker.gitlab.GitLabUtils;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.StudyRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"gitlab-test", "example"})
public class GitLabServiceTests {

  private static final String EXAMPLE_PROGRAM = "Preclinical Project B";

  @Autowired private GitLabService gitLabService;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private ProgramRepository programRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private AssayRepository assayRepository;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void createProgramGroupTest() throws Exception {
    Program program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_ID));
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_NAME));
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_PATH));
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_ID));
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_NAME));
    Assert.assertFalse(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_PATH));
    GitServerGroup group = gitLabService.createProgramGroup(program);
    Assert.assertNotNull(group);
    Assert.assertEquals(group.getName(), program.getName());
    Assert.assertEquals(group.getPath(), GitLabUtils.getPathFromName(program.getName()));
    program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_ID));
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_NAME));
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_PATH));
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_ID));
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_NAME));
    Assert.assertTrue(program.getAttributes().containsKey(GitAttributes.GROUP_PARENT_PATH));

    GitServerGroup group2 = gitLabService.findProgramGroup(program).orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(group2);
    Assert.assertEquals(group.getGroupId(), group2.getGroupId());
  }

  @Test
  public void createStudyRepositoryTest() throws Exception {
    createProgramGroupTest();
    Program program = programRepository.findByName(EXAMPLE_PROGRAM)
        .orElseThrow(RecordNotFoundException::new);
    Study study = studyRepository.findByProgramId(program.getId()).get(0);
    Assert.assertNotNull(study);
    GitServerRepository repository = gitLabService.createStudyRepository(study);
    Assert.assertNotNull(repository);
    Assert.assertEquals(repository.getName(), GitLabUtils.getStudyProjectName(study));
    Assert.assertEquals(repository.getPath(), GitLabUtils.getStudyProjectPath(study));

    GitServerRepository repository2 = gitLabService.findStudyRepository(study).orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(repository2);
    Assert.assertEquals(repository.getRepositoryId(), repository2.getRepositoryId());
  }

  @Test
  public void createAssayRepository() throws Exception {
    createProgramGroupTest();
    Assay assay = assayRepository.findByCode("PPB-10001-001").orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(assay);
    GitServerRepository repository = gitLabService.createAssayRepository(assay);
    Assert.assertNotNull(repository);
    Assert.assertEquals(repository.getName(), GitLabUtils.getAssayProjectName(assay));
    Assert.assertEquals(repository.getPath(), GitLabUtils.getAssayProjectPath(assay));

    GitServerRepository repository2 = gitLabService.findAssayRepository(assay).orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(repository2);
    Assert.assertEquals(repository.getRepositoryId(), repository2.getRepositoryId());
  }

}
