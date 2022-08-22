package io.studytracker.search;

import java.io.Serializable;

public interface AssaySearchDocument<I extends Serializable> extends SearchDocument<I> {

  String getCode();

  String getName();

  String getDescription();
}
