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

package io.studytracker.config;

import io.studytracker.example.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"example"})
public class ExampleDataConfiguration {

  @Bean
  public ExampleDataRunner exampleDataGenerator() {
    return new ExampleDataRunner();
  }

  @Bean
  public ExampleUserGenerator exampleUserGenerator() {
    return new ExampleUserGenerator();
  }

  @Bean
  public ExampleProgramGenerator exampleProgramGenerator() {
    return new ExampleProgramGenerator();
  }

  @Bean
  public ExampleKeywordGenerator exampleKeywordGenerator() {
    return new ExampleKeywordGenerator();
  }

  @Bean
  public ExampleCollaboratorGenerator exampleCollaboratorGenerator() {
    return new ExampleCollaboratorGenerator();
  }

  @Bean
  public ExampleStudyGenerator exampleStudyGenerator() {
    return new ExampleStudyGenerator();
  }

  @Bean
  public ExampleAssayTypeGenerator exampleAssayTypeGenerator() {
    return new ExampleAssayTypeGenerator();
  }

  @Bean
  public ExampleAssayGenerator exampleAssayGenerator() {
    return new ExampleAssayGenerator();
  }

  @Bean
  public ExampleStudyCollectionGenerator exampleStudyCollectionGenerator() {
    return new ExampleStudyCollectionGenerator();
  }

  @Bean
  public ExampleStorageFolderGenerator exampleStorageFolderGenerator() {
    return new ExampleStorageFolderGenerator();
  }

  @Bean
  public ExampleIntegrationGenerator exampleIntegrationGenerator() {
    return new ExampleIntegrationGenerator();
  }

  @Bean
  public ExampleGitRepositoryGenerator exampleGitRepositoryGenerator() {
    return new ExampleGitRepositoryGenerator();
  }

}
