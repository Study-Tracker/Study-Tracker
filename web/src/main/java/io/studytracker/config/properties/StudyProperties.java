package io.studytracker.config.properties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "study")
@Validated
@Getter
@Setter
public class StudyProperties {

  @NotNull
  @Min(1)
  private Integer studyCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer studyCodeMinDigits;

  @NotNull
  @Min(1)
  private Integer assayCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer assayCodeMinDigits;

  @NotNull
  @Min(1)
  private Integer externalCodeCounterStart;

  @NotNull
  @Min(3)
  private Integer externalCodeMinDigits;

}
