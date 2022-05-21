package io.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingUser {

  private String id;
  private String name;
  private String handle;
  private String email;

}
