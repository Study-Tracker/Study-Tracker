package io.studytracker.mapstruct.dto.response;

import java.net.URL;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExternalLinkDto {

  private Long id;
  @NotNull private String label;
  @NotNull private URL url;
}
