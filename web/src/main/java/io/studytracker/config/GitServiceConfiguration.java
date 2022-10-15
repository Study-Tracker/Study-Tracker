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

package io.studytracker.config;

import io.studytracker.gitlab.GitLabOptions;
import io.studytracker.gitlab.GitLabRestClient;
import io.studytracker.gitlab.GitLabService;
import java.net.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GitServiceConfiguration {

  @Configuration
  @ConditionalOnProperty(name = "git.mode", havingValue = "gitlab")
  public static class GitLabConfiguration {

    @Autowired private Environment environment;

    @Bean
    public GitLabOptions gitLabOptions() {
      GitLabOptions options = new GitLabOptions();
      options.setRootUrl(environment.getRequiredProperty("gitlab.url", URL.class));
      options.setUsername(environment.getRequiredProperty("gitlab.username"));
      options.setPassword(environment.getRequiredProperty("gitlab.password"));
      if (environment.containsProperty("gitlab.client-id")) {
        options.setClientId(environment.getRequiredProperty("gitlab.client-id"));
      }
      if (environment.containsProperty("gitlab.client-secret")) {
        options.setClientSecret(environment.getRequiredProperty("gitlab.client-secret"));
      }
      return options;
    }

    @Bean
    public GitLabRestClient gitLabRestClient() {
      return new GitLabRestClient(new RestTemplate(), gitLabOptions());
    }

    @Bean
    public GitLabService gitLabService() {
      return new GitLabService(gitLabRestClient());
    }

  }

}
