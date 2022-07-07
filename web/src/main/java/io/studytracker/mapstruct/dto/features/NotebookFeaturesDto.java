package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotebookFeaturesDto {

  private String mode = "none";

  @JsonProperty("isEnabled")
  public boolean isEnabled() {
    return mode != null && !mode.equals("none");
  }

}
