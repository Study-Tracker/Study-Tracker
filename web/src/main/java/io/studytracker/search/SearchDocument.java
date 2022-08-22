package io.studytracker.search;

import java.io.Serializable;

public interface SearchDocument <I extends Serializable> {

  I getId();

}
