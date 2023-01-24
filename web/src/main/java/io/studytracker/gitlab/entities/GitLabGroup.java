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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabGroup {

  private Integer id;
  @JsonProperty("web_url") private String webUrl;
  private String name;
  private String path;
  private String description;
  @JsonProperty("full_name") private String fullName;
  @JsonProperty("full_path") private String fullPath;
  @JsonProperty("created_at") private Date createdAt;
  @JsonProperty("parent_id") private Integer parentId;
  private String visibility;
  @JsonProperty("share_with_group_lock") private Boolean shareWithGroupLock;
  @JsonProperty("require_two_factor_authentication") private Boolean requireTwoFactorAuthentication;
  @JsonProperty("two_factor_grace_period") private Integer twoFactorGracePeriod;
  @JsonProperty("project_creation_level") private String projectCreationLevel;
  @JsonProperty("auto_devops_enabled") private Boolean autoDevopsEnabled;
  @JsonProperty("subgroup_creation_level") private String subgroupCreationLevel;
  @JsonProperty("emails_disabled") private Boolean emailsDisabled;
  @JsonProperty("mentions_disabled") private Boolean mentionsDisabled;
  @JsonProperty("lfs_enabled") private Boolean lfsEnabled;
  @JsonProperty("default_branch_protection") private Integer defaultBranchProtection;
  @JsonProperty("avatar_url") private String avatarUrl;
  @JsonProperty("request_access_enabled") private Boolean requestAccessEnabled;
  @JsonProperty("ldap_cn") private String ldapCn;
  @JsonProperty("ldap_access") private String ldapAccess;
  @JsonProperty("marked_for_deletion_on") private Date markedForDeletionOn;

}
