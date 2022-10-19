package io.studytracker.gitlab.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNamespace {

  private Integer id;
  private String name;
  private String path;
  private String kind;
  @JsonProperty("full_path") private String fullPath;
  @JsonProperty("parent_id") private Integer parentId;
  @JsonProperty("avatar_url") private String avatarUrl;
  @JsonProperty("web_url") private String webUrl;

}
