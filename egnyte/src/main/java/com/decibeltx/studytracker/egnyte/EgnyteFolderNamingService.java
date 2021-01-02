package com.decibeltx.studytracker.egnyte;

import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Program;
import com.decibeltx.studytracker.core.model.Study;
import com.decibeltx.studytracker.core.service.NamingOptions;
import com.decibeltx.studytracker.core.service.impl.NamingServiceImpl;

public class EgnyteFolderNamingService extends NamingServiceImpl {

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
