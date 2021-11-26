package com.decibeltx.studytracker.search;

import java.io.Serializable;

public interface StudySearchDocument<I extends Serializable> {

  I getId();
  String getCode();
  String getName();
  String getDescription();

}
