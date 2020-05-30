package com.decibeltx.studytracker.cli;

import com.beust.jcommander.Parameter;
import lombok.Data;

@Data
public class BaseArguments {

  @Parameter(names = {"-h", "--help"}, description = "Displays usage dialog")
  private boolean help = false;

}
