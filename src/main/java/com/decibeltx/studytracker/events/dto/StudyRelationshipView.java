package com.decibeltx.studytracker.events.dto;

import com.decibeltx.studytracker.model.StudyRelationship;

public final class StudyRelationshipView {

  private String type;

  private StudyRelationshipView() {
  }

  public static StudyRelationshipView from(StudyRelationship relationship) {
    StudyRelationshipView view = new StudyRelationshipView();
    view.setType(relationship.getType().toString());
    return view;
  }

  public String getType() {
    return type;
  }

  private void setType(String type) {
    this.type = type;
  }
}
