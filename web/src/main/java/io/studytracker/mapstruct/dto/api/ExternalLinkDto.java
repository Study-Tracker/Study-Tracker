package io.studytracker.mapstruct.dto.api;

import java.net.URL;
import lombok.Data;

@Data
public class ExternalLinkDto {

  private Long id;
  private Long studyId;
  private String label;
  private URL url;
}
