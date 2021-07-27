package com.decibeltx.studytracker.mapstruct.dto;

import java.net.URL;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExternalLinkDto {

  private Long id;
  @NotNull private String label;
  @NotNull private URL url;

}
