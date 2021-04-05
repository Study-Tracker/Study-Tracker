package com.decibeltx.studytracker.benchling.eln.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingArchiveRecord {

  private String reason;

}
