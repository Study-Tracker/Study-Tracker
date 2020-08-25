package com.decibeltx.studytracker.cli.argument;

import com.beust.jcommander.Parameter;
import lombok.Data;

@Data
public class BaseArguments {

  @Parameter(names = {"-u", "--user"}, description = "Username")
  private String username;

  @Parameter(names = {"-p", "--password"}, description = "Password")
  private String password;

  @Parameter(names = {"-h", "--help"}, description = "Displays usage dialog")
  private boolean help = false;

}
