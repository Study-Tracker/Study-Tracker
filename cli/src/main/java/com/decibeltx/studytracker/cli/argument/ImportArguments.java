package com.decibeltx.studytracker.cli.argument;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Parameters(commandDescription = "Allows populating the database with data from files.")
public class ImportArguments {

  public static final String COMMAND = "import";

  @Parameter(names = {"-D",
      "--drop-database"}, description = "Drops the database before performing additional operations.")
  private boolean dropDatabase = false;

  @Parameter(description = "List of files containing records to be imported.")
  private List<String> files = new ArrayList<>();

}
