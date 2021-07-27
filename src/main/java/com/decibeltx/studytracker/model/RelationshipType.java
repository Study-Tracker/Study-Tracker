package com.decibeltx.studytracker.model;

public enum RelationshipType {

  IS_RELATED_TO,
  IS_PARENT_OF,
  IS_CHILD_OF,
  IS_BLOCKING,
  IS_BLOCKED_BY,
  IS_PRECEDED_BY,
  IS_SUCCEEDED_BY;

  public static RelationshipType getInverse(RelationshipType type) {
    switch (type) {
      case IS_RELATED_TO:
        return IS_RELATED_TO;
      case IS_PARENT_OF:
        return IS_CHILD_OF;
      case IS_CHILD_OF:
        return IS_PARENT_OF;
      case IS_BLOCKING:
        return IS_BLOCKED_BY;
      case IS_BLOCKED_BY:
        return IS_BLOCKING;
      case IS_PRECEDED_BY:
        return IS_SUCCEEDED_BY;
      case IS_SUCCEEDED_BY:
        return IS_PRECEDED_BY;
      default:
        return IS_RELATED_TO;
    }
  }

}
