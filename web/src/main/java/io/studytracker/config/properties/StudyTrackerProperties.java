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

package io.studytracker.config.properties;

import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "")
@Validated
@Getter
@Setter
@ToString
public class StudyTrackerProperties {

  @Valid
  private final ApplicationProperties application = new ApplicationProperties();

  @Valid
  private final ServerProperties server = new ServerProperties();

  @Valid
  private final AdminProperties admin = new AdminProperties();

  @Valid
  private final DatabaseProperties db = new DatabaseProperties();

  @Valid
  private final EventsProperties events = new EventsProperties();

  @Valid
  private final AWSProperties aws = new AWSProperties();

  @Valid
  private final EmailProperties email = new EmailProperties();

  @Valid
  private final NotebookProperties notebook = new NotebookProperties();

  @Valid
  private final StorageProperties storage = new StorageProperties();

  @Valid
  private final EgnyteProperties egnyte = new EgnyteProperties();

  @Valid
  private final StudyProperties study = new StudyProperties();

  @Valid
  private final SearchProperties search = new SearchProperties();

  @Valid
  private final SingleSignOnProperties sso = new SingleSignOnProperties();

  @Valid
  private final GitProperties git = new GitProperties();

  @Valid
  private final GitLabProperties gitlab = new GitLabProperties();


  @Getter
  @Setter
  @ToString
  public static class ServerProperties {
    private Integer port;
  }

}
