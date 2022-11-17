package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class AWSFeaturesDto {

  private String region;

  private String accessKey;

  private String defaultS3StudyLocation;

  private String eventBridgeBus;

  @JsonProperty("isEnabled")
  public boolean isEnabled() {
    return StringUtils.hasText(region) && StringUtils.hasText(defaultS3StudyLocation);
  }

}
