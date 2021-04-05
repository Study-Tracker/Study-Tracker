package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.model.Assay;
import com.decibeltx.studytracker.model.Program;
import com.decibeltx.studytracker.model.Study;

/**
 * Service definition for naming study folders, notebook entries, and more.
 */
public interface NamingService {

  /**
   * Generates a new {@link Study} code, given that study's record.
   *
   * @param study
   * @return
   */
  String generateStudyCode(Study study);

  /**
   * Generates an external study code for a {@link Study}.
   *
   * @param study
   * @return
   */
  String generateExternalStudyCode(Study study);

  /**
   * Generates a new {@link Assay} code, given that assay record.
   *
   * @param assay
   * @return
   */
  String generateAssayCode(Assay assay);

  /**
   * Returns a {@link Study} object's derived storage folder name.
   *
   * @param study
   * @return
   */
  String getStudyStorageFolderName(Study study);

  /**
   * Returns a {@link Assay} object's derived storage folder name.
   *
   * @param assay
   * @return
   */
  String getAssayStorageFolderName(Assay assay);

  /**
   * Returns a {@link Program} object's derived storage folder name.
   *
   * @param program
   * @return
   */
  String getProgramStorageFolderName(Program program);

  String getStudyNotebookFolderName(Study study);

  String getAssayNotebookFolderName(Assay assay);

  String getProgramNotebookFolderName(Program program);

}
