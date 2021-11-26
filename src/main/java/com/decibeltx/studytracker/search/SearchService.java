package com.decibeltx.studytracker.search;

import com.decibeltx.studytracker.model.Study;
import java.io.Serializable;
import java.util.Collection;

public interface SearchService<T extends StudySearchDocument<I>, I extends Serializable> {

  StudySearchHits<T> search(String keyword);

  StudySearchHits<T> search(String keyword, String field);

  void indexStudy(Study study);

  void indexStudies(Collection<Study> studies);

}
