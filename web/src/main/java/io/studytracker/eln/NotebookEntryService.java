package io.studytracker.eln;

import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import java.util.List;
import java.util.Optional;

public interface NotebookEntryService {

  /**
   * Fetches a list of notebook entry templates.
   *
   * @return
   */
  List<NotebookTemplate> findEntryTemplates();

  /**
   * Returns all notebook templates that match the provided keyword fragment.
   * @param keyword keyword fragment
   * @return list of notebook templates
   */
  List<NotebookTemplate> searchNotebookTemplates(String keyword);

  /**
   * Finds and returns a {@link NotebookTemplate} if one exists with the provided ID.
   *
   * @param id
   * @return
   */
  Optional<NotebookTemplate> findEntryTemplateById(String id);

  /**
   * Creates a blank notebook entry for a study in the ELN and returns a {@link NotebookEntry}.
   *
   * @param study
   * @return
   * @throws NotebookException
   */
  NotebookEntry createStudyNotebookEntry(Study study) throws NotebookException;

  /**
   * Creates a blank notebook entry from the provided template for a study in the ELN and returns a
   * {@link NotebookEntry}.
   *
   * @param study
   * @return
   * @throws NotebookException
   */
  NotebookEntry createStudyNotebookEntry(Study study, NotebookTemplate template)
      throws NotebookException;

  /**
   * Creates a blank notebook entry for an assay in the ELN and returns a {@link NotebookEntry}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookEntry createAssayNotebookEntry(Assay assay) throws NotebookException;

  /**
   * Creates a blank notebook entry from the provided template for an assay in the ELN and returns a
   * {@link NotebookEntry}.
   *
   * @param assay
   * @return
   * @throws NotebookException
   */
  NotebookEntry createAssayNotebookEntry(Assay assay, NotebookTemplate template)
      throws NotebookException;


}
