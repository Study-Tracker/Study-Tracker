package com.decibeltx.studytracker.exception;

public class MalformedEntityException extends StudyTrackerException {

  public MalformedEntityException() {
  }

  public MalformedEntityException(String message) {
    super(message);
  }

  public MalformedEntityException(String message, Throwable cause) {
    super(message, cause);
  }

  public MalformedEntityException(Throwable cause) {
    super(cause);
  }

  public MalformedEntityException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
