package com.decibeltx.studytracker.core.service.impl;

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Collaborator;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.repository.AssayRepository;
import com.decibeltx.studytracker.core.repository.StudyRepository;
import com.decibeltx.studytracker.core.service.NamingOptions;
import com.decibeltx.studytracker.core.service.NamingService;
import com.decibeltx.studytracker.core.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;

public class NamingServiceImpl implements NamingService {

  private final NamingOptions options;

  @Autowired
  private ProgramService programService;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private AssayRepository assayRepository;

  public NamingServiceImpl(NamingOptions options) {
    this.options = options;
  }

  @Override
  public String generateStudyCode(Study study) {
    if (study.isLegacy()) {
      throw new StudyTrackerException("Legacy studies do not receive new study codes.");
    }
    Program program = study.getProgram();
    Integer count = options.getStudyCodeCounterStart();
    for (Program p : programService.findByCode(program.getCode())) {
      count = count + (studyRepository.findActiveProgramStudies(p.getId())).size();
    }
    return program.getCode() + "-"
        + String.format("%0" + options.getStudyCodeMinimumDigits() + "d", count);
  }

  @Override
  public String generateExternalStudyCode(Study study) {
    Collaborator collaborator = study.getCollaborator();
    if (collaborator == null) {
      throw new StudyTrackerException("External studies require a valid collaborator reference.");
    }
    int count = options.getExternalStudyCodeCounterStart()
        + studyRepository.findByExternalCodePrefix(collaborator.getCode() + "-").size();
    return collaborator.getCode() + "-"
        + String.format("%0" + options.getExternalStudyCodeMinimumDigits() + "d", count);
  }

  @Override
  public String generateAssayCode(Assay assay) {
    Study study = assay.getStudy();
    String prefix = study.getProgram().getCode() + "-";
    int count = options.getAssayCodeCounterStart()
        + assayRepository.findByCodePrefix(prefix).size();
    return study.getCode() + "-"
        + String.format("%0" + options.getAssayCodeMinimumDigits() + "d", count);
  }

  @Override
  public String getStudyStorageFolderName(Study study) {
    return study.getCode() + " - " + study.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  @Override
  public String getAssayStorageFolderName(Assay assay) {
    return assay.getCode() + " - " + assay.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  @Override
  public String getProgramStorageFolderName(Program program) {
    return program.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  @Override
  public String getStudyNotebookFolderName(Study study) {
    return study.getCode() + ": " + study.getName();
  }

  @Override
  public String getAssayNotebookFolderName(Assay assay) {
    return assay.getCode() + ": " + assay.getName();
  }

  @Override
  public String getProgramNotebookFolderName(Program program) {
    return program.getName();
  }
}
