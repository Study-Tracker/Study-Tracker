package com.decibeltx.studytracker.cli.executor.importer;

import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.service.CollaboratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollaboratorImporter extends RecordImporter<Collaborator> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollaboratorImporter.class);

  @Autowired
  private CollaboratorService collaboratorService;

  public CollaboratorImporter() {
    super(Collaborator.class);
  }

  @Override
  void importRecord(Collaborator record) throws Exception {
    if (collaboratorService.findByLabel(record.getLabel()).isPresent()) {
      LOGGER.warn(String.format("Collaborator with label '%s' already exists. This record will be "
          + "skipped.", record.getLabel()));
    } else {
      this.validate(record);
      collaboratorService.create(record);
    }
  }
}
