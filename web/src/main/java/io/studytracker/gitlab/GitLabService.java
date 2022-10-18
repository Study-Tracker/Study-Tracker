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

package io.studytracker.gitlab;

import io.studytracker.gitlab.entities.GitLabAuthenticationToken;
import io.studytracker.gitlab.entities.GitLabGroup;
import io.studytracker.gitlab.entities.GitLabNewGroupRequest;
import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitLabService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GitLabService.class);

  private final GitLabRestClient client;

  public GitLabService(GitLabRestClient gitLabRestClient) {
    this.client = gitLabRestClient;
  }

  public void createProgramGroup(Program program) {

    LOGGER.info("Creating group for program {}", program.getName());
    GitLabAuthenticationToken token = client.authenticate();

    // Check to make sure a group doesn't already exist
    if (program.getAttributes().containsKey(GitLabAttributes.NAMESPACE_ID)) {
      LOGGER.info("Group already exists for program {}", program.getName());
      return;
    }
    List<GitLabGroup> groups = client.findGroups(token.getAccessToken(), program.getName());
    if (!groups.isEmpty()) {
      LOGGER.info("Group already exists for program {}", program.getName());
      return;
    }

    // Create the group
    GitLabNewGroupRequest request = new GitLabNewGroupRequest();
    request.setName(program.getName());
    request.setPath(program.getName());
    // TODO: Continue here

  }

  public Object getProgramGroup(Program program) {
    LOGGER.info("Getting group for program {}", program.getName());
    return null;
  }

  public void createStudyRepository(Study study) {
    LOGGER.info("Creating repository for study {}", study.getName());
  }

  public Object getStudyRepository(Study study) {
    LOGGER.info("Getting repository for study {}", study.getName());
    return null;
  }

  public void createAssayRepository(Assay assay) {
    LOGGER.info("Creating repository for assay {}", assay.getName());
  }

  public Object getAssayRepository(Assay assay) {
    LOGGER.info("Getting repository for assay {}", assay.getName());
    return null;
  }

}
