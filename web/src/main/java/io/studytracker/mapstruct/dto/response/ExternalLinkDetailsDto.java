package io.studytracker.mapstruct.dto.response;

import java.net.URL;
import lombok.Data;

@Data
public class ExternalLinkDetailsDto {
  private Long id;
  private String label;
  private URL url;
}
