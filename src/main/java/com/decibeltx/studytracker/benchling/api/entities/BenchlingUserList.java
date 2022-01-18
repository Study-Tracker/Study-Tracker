package com.decibeltx.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BenchlingUserList {

  private String nextToken;
  private List<BenchlingUser> users = new ArrayList<>();

}
