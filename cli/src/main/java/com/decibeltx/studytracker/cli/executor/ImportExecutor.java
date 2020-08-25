package com.decibeltx.studytracker.cli.executor;

import com.decibeltx.studytracker.cli.argument.ImportArguments;
import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.model.User;
import com.decibeltx.studytracker.core.service.CollaboratorService;
import com.decibeltx.studytracker.core.service.ProgramService;
import com.decibeltx.studytracker.core.service.StudyService;
import com.decibeltx.studytracker.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ImportExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportExecutor.class);

  private static final List<String> VALID_FILE_FORMATS = Arrays.asList("yml", "yaml");

  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ProgramService programService;

  @Autowired
  private StudyService studyService;

  @Autowired
  private CollaboratorService collaboratorService;

  @Autowired
  private UserService userService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public void execute(ImportArguments args, User user) throws Exception {

    // Validate the input
    Path workingDir = Paths.get(System.getProperty("user.dir"));
    List<File> files = new ArrayList<>();
    for (String arg : args.getFiles()) {
      Path filePath = workingDir.resolve(arg);
      File file = filePath.toFile();
      if (!file.exists() || !file.isFile() || !file.canRead()) {
        throw new IOException("Cannot read file: " + file.getAbsolutePath());
      }
      if (!VALID_FILE_FORMATS.contains(FilenameUtils.getExtension(file.getName()))) {
        throw new IOException("Invalid file format. Currently supported file formats: YAML");
      }
      files.add(file);
    }

    // Drop the database
    if (args.isDropDatabase()) {
      dropDatabase();
    }

    // Import the data
    for (File file : files) {
      LOGGER.info("Importing records from file: " + file.getAbsolutePath());
      DatabaseRecords records;
      try {
        records = objectMapper.readValue(file, DatabaseRecords.class);
      } catch (Exception e) {
        throw new IOException("Failed to parse seed file: " + file.getAbsolutePath());
      }
      loadData(records, user);
    }

    LOGGER.info("Data import complete.");
  }

  private void loadData(DatabaseRecords seeds, User createdBy) throws StudyTrackerException {

    LOGGER.info("Inserting database seeds...");

    // Programs
    if (!seeds.getPrograms().isEmpty()) {
      LOGGER.info("Inserting program records...");
      for (Program program : seeds.getPrograms()) {
        program.setCreatedBy(createdBy);
        program.setLastModifiedBy(createdBy);
        programService.create(program);
      }
    }

    // Users
    if (!seeds.getUsers().isEmpty()) {
      LOGGER.info("Inserting user records...");
      for (User user : seeds.getUsers()) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.create(user);
      }
    }

    // Collaborators
    if (!seeds.getCollaborators().isEmpty()) {
      LOGGER.info("Inserting collaboraor records...");
      for (Collaborator collaborator : seeds.getCollaborators()) {
        collaboratorService.create(collaborator);
      }
    }

    // Studies
    if (!seeds.getStudies().isEmpty()) {
      LOGGER.info("Inserting study records...");
      for (Study study : seeds.getStudies()) {
        if (study.getCreatedBy() == null) {
          study.setCreatedBy(createdBy);
        }
        studyService.create(study);
      }
    }

    LOGGER.info("Database seeding complete.");
  }

  private void dropDatabase() {
    LOGGER.warn("Wiping database...");
    mongoTemplate.getDb().drop();
  }

}
