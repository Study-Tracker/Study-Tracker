package io.studytracker.config.properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "storage")
@Validated
@Getter
@Setter
public class StorageProperties {

  @ConfigurationModeConstraint(options = {"local", "egnyte"})
  private String mode;

  private Boolean useExisting;

  @Deprecated
  @Min(0)
  @Max(5)
  private Integer maxFolderReadDepth;

  @NotEmpty
  private String tempDir;

  private String localDir;

}
