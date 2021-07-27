package com.decibeltx.studytracker.exception;

public class InsufficientPrivilegesException extends StudyTrackerException {

  public InsufficientPrivilegesException() {
  }

  public InsufficientPrivilegesException(String message) {
    super(message);
  }

  public InsufficientPrivilegesException(String message, Throwable cause) {
    super(message, cause);
  }

  public InsufficientPrivilegesException(Throwable cause) {
    super(cause);
  }

  public InsufficientPrivilegesException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
