/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.teams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(name = "teams.enabled", havingValue = "true")
public class TeamsServiceConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public TeamsOptions teamsOptions() {
    Assert.notNull(env.getProperty("teams.username"), "Teams username is not set.");
    Assert.notNull(env.getProperty("teams.password"), "Teams password is not set.");
    Assert.notNull(env.getProperty("teams.client-id"), "Teams client ID is not set.");
    Assert.notNull(env.getProperty("teams.secret"), "Teams secret token is not set.");
    Assert.notNull(env.getProperty("teams.default-team"), "Teams default team is not set.");
    Assert.notNull(env.getProperty("teams.default-channel"), "Teams default channel is not set.");
    TeamsOptions options = new TeamsOptions();
    options.setUsername(env.getRequiredProperty("teams.username"));
    options.setPassword(env.getRequiredProperty("teams.password"));
    options.setClientId(env.getRequiredProperty("teams.client-id"));
    options.setSecret(env.getRequiredProperty("teams.secret"));
    options.setDefaultTeam(env.getRequiredProperty("teams.default-team"));
    options.setDefaultChannel(env.getRequiredProperty("teams.default-channel"));
    options.setEnabled(env.getRequiredProperty("teams.enabled", boolean.class));
    return options;
  }

  @Bean
  public TeamsBetaRestApiClient teamsRestApiService() {
    return new TeamsBetaRestApiClient(teamsOptions());
  }

  @Bean
  public TeamsMessagingService teamsStudyMessagingService() {
    return new TeamsMessagingService();
  }

}
