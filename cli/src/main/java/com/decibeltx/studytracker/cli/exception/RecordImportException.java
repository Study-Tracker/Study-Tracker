package com.decibeltx.studytracker.cli.exception;

public class RecordImportException extends Exception {

  public RecordImportException() {
  }

  public RecordImportException(String message) {
    super(message);
  }

  public RecordImportException(String message, Throwable cause) {
    super(message, cause);
  }

  public RecordImportException(Throwable cause) {
    super(cause);
  }

  public RecordImportException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
