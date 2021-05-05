package com.decibeltx.studytracker.exception;

public class UnknownUserException extends RuntimeException {

  public UnknownUserException() {
  }

  public UnknownUserException(String message) {
    super(message);
  }

  public UnknownUserException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnknownUserException(Throwable cause) {
    super(cause);
  }

  public UnknownUserException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
