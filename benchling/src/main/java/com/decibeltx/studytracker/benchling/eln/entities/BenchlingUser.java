package com.decibeltx.studytracker.benchling.eln.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingUser {

  private String id;

  private String name;

  private String handle;

}
