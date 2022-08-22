package io.studytracker.search;

import java.io.Serializable;

public interface StudySearchDocument<I extends Serializable> extends SearchDocument<I> {

  String getCode();

  String getName();

  String getDescription();
}
