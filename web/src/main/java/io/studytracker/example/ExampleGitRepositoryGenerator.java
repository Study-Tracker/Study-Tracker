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

package io.studytracker.example;

import io.studytracker.model.GitRepository;
import io.studytracker.repository.GitGroupRepository;
import io.studytracker.repository.GitLabGroupRepository;
import io.studytracker.repository.GitLabRepositoryRepository;
import io.studytracker.repository.GitRepositoryRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleGitRepositoryGenerator implements ExampleDataGenerator<GitRepository> {

  @Autowired private GitRepositoryRepository gitRepositoryRepository;
  @Autowired private GitGroupRepository gitGroupRepository;
  @Autowired private GitLabGroupRepository gitLabGroupRepository;
  @Autowired private GitLabRepositoryRepository gitLabRepositoryRepository;

  @Override
  public List<GitRepository> generateData(Object... args) throws Exception {
    return null;
  }

  @Override
  public void deleteData() {
    gitLabRepositoryRepository.deleteAll();
    gitRepositoryRepository.deleteAll();
    gitLabGroupRepository.deleteAll();
    gitGroupRepository.deleteAll();
  }
}
