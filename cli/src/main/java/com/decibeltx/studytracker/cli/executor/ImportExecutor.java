package com.decibeltx.studytracker.cli.executor;

import com.decibeltx.studytracker.cli.argument.ImportArguments;
import com.decibeltx.studytracker.cli.executor.importer.CollaboratorImporter;
import com.decibeltx.studytracker.cli.executor.importer.KeywordImporter;
import com.decibeltx.studytracker.cli.executor.importer.ProgramImporter;
import com.decibeltx.studytracker.cli.executor.importer.StudyImporter;
import com.decibeltx.studytracker.cli.executor.importer.UserImporter;
import com.decibeltx.studytracker.core.model.User;
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
import org.springframework.stereotype.Component;

@Component
public class ImportExecutor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImportExecutor.class);

  private static final List<String> VALID_FILE_FORMATS = Arrays.asList("yml", "yaml");

  private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

  @Autowired
  private ProgramImporter programImporter;

  @Autowired
  private UserImporter userImporter;

  @Autowired
  private CollaboratorImporter collaboratorImporter;

  @Autowired
  private KeywordImporter keywordImporter;

  @Autowired
  private StudyImporter studyImporter;

  @Autowired
  private MongoTemplate mongoTemplate;

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

  private void loadData(DatabaseRecords seeds, User createdBy) throws Exception {

    LOGGER.info("Inserting database seeds...");

    // Programs
    if (!seeds.getPrograms().isEmpty()) {
      programImporter.importRecords(seeds.getPrograms(), createdBy);
    }

    // Users
    if (!seeds.getUsers().isEmpty()) {
      userImporter.importRecords(seeds.getUsers());
    }

    // Collaborators
    if (!seeds.getCollaborators().isEmpty()) {
      collaboratorImporter.importRecords(seeds.getCollaborators());
    }

    // Keywords
    if (!seeds.getKeywords().isEmpty()) {
      keywordImporter.importRecords(seeds.getKeywords());
    }

    // Studies
    if (!seeds.getStudies().isEmpty()) {
      studyImporter.importRecords(seeds.getStudies(), createdBy);
    }

    LOGGER.info("Database seeding complete.");
  }

  private void dropDatabase() {
    LOGGER.warn("Wiping database...");
    mongoTemplate.getDb().drop();
  }

}
