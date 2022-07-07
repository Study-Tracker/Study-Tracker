package io.studytracker.mapstruct.dto.features;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StorageFeaturesDto {

  private String mode;

  @JsonProperty("label")
  public String getLabel() {
    switch (mode) {
      case "egnyte":
        return "Egnyte";
      case "local":
        return "File System";
      default:
        return mode;
    }
  }

}
