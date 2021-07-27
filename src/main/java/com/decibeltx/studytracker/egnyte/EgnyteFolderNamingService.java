package com.decibeltx.studytracker.egnyte;

import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.service.NamingOptions;
import com.decibeltx.studytracker.service.NamingService;

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
