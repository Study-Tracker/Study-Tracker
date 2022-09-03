package io.studytracker.mapstruct.dto.form;

import java.net.URL;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExternalLinkFormDto {
  private Long id;
  @NotNull private String label;
  @NotNull private URL url;
}
