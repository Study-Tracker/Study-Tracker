package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchFeaturesDto {

  private String mode;

  @JsonProperty("isEnabled")
  public boolean isEnabled() {
    return mode != null && !mode.equals("none");
  }

}
