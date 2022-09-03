package io.studytracker.mapstruct.dto.api;

import java.net.URL;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExternalLinkPayloadDto {
  private Long id;
  @NotNull private Long studyId;
  @NotNull private String label;
  @NotNull private URL url;
}
