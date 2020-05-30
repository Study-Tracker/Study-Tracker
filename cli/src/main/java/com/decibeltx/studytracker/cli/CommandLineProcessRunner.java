package com.decibeltx.studytracker.cli;

import com.beust.jcommander.JCommander;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineProcessRunner implements CommandLineRunner {

  @Autowired
  private ImportExecutor importExecutor;

  @Override
  public void run(String[] args) throws Exception {

    BaseArguments baseArguments = new BaseArguments();
    ImportArguments importArguments = new ImportArguments();

    JCommander jc = JCommander.newBuilder()
        .addObject(baseArguments)
        .addCommand(ImportArguments.COMMAND, importArguments)
        .build();

    jc.parse(args);

    if (baseArguments.isHelp()) {
      jc.usage();
      return;
    }

    String command = jc.getParsedCommand();

    // Import command
    if (ImportArguments.COMMAND.equals(command)) {
      importExecutor.execute(importArguments);
    } else {
      jc.usage();
    }

  }

}
