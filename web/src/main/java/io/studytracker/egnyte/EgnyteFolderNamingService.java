package io.studytracker.egnyte;

import io.studytracker.model.Assay;
import io.studytracker.model.Program;
import io.studytracker.model.Study;
import io.studytracker.service.NamingOptions;
import io.studytracker.service.NamingService;

public class EgnyteFolderNamingService extends NamingService {

  public EgnyteFolderNamingService(NamingOptions options) {
    super(options);
  }

  @Override
  public String getStudyStorageFolderName(Study study) {
    return super.getStudyStorageFolderName(study)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  @Override
  public String getAssayStorageFolderName(Assay assay) {
    return super.getAssayStorageFolderName(assay)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  @Override
  public String getProgramStorageFolderName(Program program) {
    return super.getProgramStorageFolderName(program)
        .replaceAll("_", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }
}
