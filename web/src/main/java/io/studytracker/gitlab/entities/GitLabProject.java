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
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabProject {

  private Integer id;
  private String name;
  private String description;
  @JsonProperty("name_with_namespace") private String nameWithNamespace;
  @JsonProperty("path_with_namespace") private String pathWithNamespace;
  private String path;
  @JsonProperty("created_at") private Date createdAt;
  @JsonProperty("default_branch") private String defaultBranch;
  @JsonProperty("tag_list") private List<String> tagList;
  @JsonProperty("ssh_url_to_repo") private String sshUrlToRepo;
  @JsonProperty("http_url_to_repo") private String httpUrlToRepo;
  @JsonProperty("web_url") private String webUrl;
  @JsonProperty("readme_url") private String readmeUrl;
  @JsonProperty("avatar_url") private String avatarUrl;
  @JsonProperty("star_count") private Long starCount;
  @JsonProperty("forks_count") private Long forksCount;
  @JsonProperty("last_activity_at") private Date lastActivityAt;
  private GitLabNamespace namespace;
  @JsonProperty("empty_repo") private boolean emptyRepo;
  @JsonProperty("archived") private boolean archived;
  @JsonProperty("visibility") private String visibility;
  private GitLabUser owner;

}
