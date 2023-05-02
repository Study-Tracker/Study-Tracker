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

import io.studytracker.repository.AwsIntegrationRepository;
import io.studytracker.repository.EgnyteIntegrationRepository;
import io.studytracker.repository.GitLabIntegrationRepository;
import io.studytracker.repository.MSGraphIntegrationRepository;
import io.studytracker.repository.SharePointSiteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class ExampleIntegrationGenerator implements ExampleDataGenerator<Object> {

  @Autowired private EgnyteIntegrationRepository egnyteIntegrationRepository;
  @Autowired private GitLabIntegrationRepository gitLabIntegrationRepository;
  @Autowired private AwsIntegrationRepository awsIntegrationRepository;
  @Autowired private MSGraphIntegrationRepository msGraphIntegrationRepository;
  @Autowired private SharePointSiteRepository sharePointSiteRepository;

  @Override
  public List<Object> generateData(Object... args) throws Exception {
    return null;
  }

  @Override
  public void deleteData() {
    sharePointSiteRepository.deleteAll();
    egnyteIntegrationRepository.deleteAll();
    gitLabIntegrationRepository.deleteAll();
    awsIntegrationRepository.deleteAll();
    msGraphIntegrationRepository.deleteAll();
  }
}
