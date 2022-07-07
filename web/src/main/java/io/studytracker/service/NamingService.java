package io.studytracker.service;

import io.studytracker.exception.StudyTrackerException;
import io.studytracker.model.Assay;
import io.studytracker.model.Collaborator;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.repository.AssayRepository;
import io.studytracker.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/** Service definition for naming study folders, notebook entries, and more. */
public class NamingService {

  private final NamingOptions options;

  private ProgramService programService;

  private StudyRepository studyRepository;

  private AssayRepository assayRepository;

  public NamingService(NamingOptions options) {
    this.options = options;
  }

  /**
   * Generates a new {@link Study} code, given that study's record.
   *
   * @param study
   * @return
   */
  public String generateStudyCode(Study study) {
    if (study.isLegacy()) {
      throw new StudyTrackerException("Legacy studies do not receive new study codes.");
    }
    Program program = study.getProgram();
    Integer count = options.getStudyCodeCounterStart();
    for (Program p : programService.findByCode(program.getCode())) {
      count = count + (studyRepository.findActiveProgramStudies(p.getId())).size();
    }
    return program.getCode()
        + "-"
        + String.format("%0" + options.getStudyCodeMinimumDigits() + "d", count);
  }

  /**
   * Generates an external study code for a {@link Study}.
   *
   * @param study
   * @return
   */
  public String generateExternalStudyCode(Study study) {
    Collaborator collaborator = study.getCollaborator();
    if (collaborator == null) {
      throw new StudyTrackerException("External studies require a valid collaborator reference.");
    }
    int count =
        options.getExternalStudyCodeCounterStart()
            + studyRepository.findByExternalCodePrefix(collaborator.getCode() + "-").size();
    return collaborator.getCode()
        + "-"
        + String.format("%0" + options.getExternalStudyCodeMinimumDigits() + "d", count);
  }

  /**
   * Generates a new {@link Assay} code, given that assay record.
   *
   * @param assay
   * @return
   */
  public String generateAssayCode(Assay assay) {
    Study study = assay.getStudy();
    String prefix = study.getCode().split("-")[0] + "-";
    long count = options.getAssayCodeCounterStart() + assayRepository.countByCodePrefix(prefix);
    return study.getCode()
        + "-"
        + String.format("%0" + options.getAssayCodeMinimumDigits() + "d", count);
  }

  /**
   * Returns a {@link Study} object's derived storage folder name.
   *
   * @param study
   * @return
   */
  public String getStudyStorageFolderName(Study study) {
    return study.getCode() + " - " + study.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  /**
   * Returns a {@link Assay} object's derived storage folder name.
   *
   * @param assay
   * @return
   */
  public String getAssayStorageFolderName(Assay assay) {
    return assay.getCode() + " - " + assay.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  /**
   * Returns a {@link Program} object's derived storage folder name.
   *
   * @param program
   * @return
   */
  public String getProgramStorageFolderName(Program program) {
    return program.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
  }

  public String getStudyNotebookFolderName(Study study) {
    return study.getCode() + ": " + study.getName();
  }

  public String getAssayNotebookFolderName(Assay assay) {
    return assay.getCode() + ": " + assay.getName();
  }

  public String getProgramNotebookFolderName(Program program) {
    return program.getName();
  }

  @Autowired
  public void setProgramService(@Lazy ProgramService programService) {
    this.programService = programService;
  }

  @Autowired
  public void setStudyRepository(@Lazy StudyRepository studyRepository) {
    this.studyRepository = studyRepository;
  }

  @Autowired
  public void setAssayRepository(@Lazy AssayRepository assayRepository) {
    this.assayRepository = assayRepository;
  }
}
