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

package io.studytracker.gitlab.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabUser {

  private Integer id;
  private String username;
  private String name;
  private String state;
  private String avatarUrl;
  private String webUrl;
  private Date createdAt;
  private Date confirmedAt;
  private Date lastActivityOn;
  private Date currentSignInAt;
  private String organization;
  private String jobTitle;
  private boolean bot;
  private String email;
  private String commitEmail;
  private boolean external;
  private boolean isAdmin;
  private Integer namespaceId;
  private String bio;
  private String location;
  private boolean isAuditor;
  private boolean usingLicenseSeat;
  private String note;

}
