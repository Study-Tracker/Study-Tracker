package io.studytracker.search;

import java.io.Serializable;

public interface PowerSearchDocument<I extends Serializable> extends SearchDocument<I>{
  DocumentType getType();
  Object getData();
}
