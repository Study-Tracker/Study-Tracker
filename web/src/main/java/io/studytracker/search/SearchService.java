package io.studytracker.search;

import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import java.util.Collection;

public interface SearchService {

  GenericSearchHits<? extends PowerSearchDocument<?>> search(String keyword);

  GenericSearchHits<? extends PowerSearchDocument<?>> search(String keyword, String field);

  GenericSearchHits<? extends StudySearchDocument<?>> searchStudies(String keyword);
  GenericSearchHits<? extends StudySearchDocument<?>> searchStudies(String keyword, String field);

  GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword);
  GenericSearchHits<? extends AssaySearchDocument<?>> searchAssays(String keyword, String field);

  void indexStudy(Study study);

  void indexStudies(Collection<Study> studies);

  void indexAssay(Assay assay);

  void indexAssays(Collection<Assay> assays);
}
