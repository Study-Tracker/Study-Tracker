package io.studytracker.gitlab.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNewProjectRequest {

  private @NotEmpty String name;
  private @NotEmpty String description;
  @JsonProperty("namespace_id") private @NotNull Integer namespaceId;
  @JsonProperty("auto_devops_enabled") private boolean autoDevopsEnabled = false;
  @JsonProperty("initialize_with_readme") private boolean initializeWithReadme = true;

}
