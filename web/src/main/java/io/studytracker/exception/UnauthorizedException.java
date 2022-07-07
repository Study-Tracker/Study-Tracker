package io.studytracker.exception;

public class UnauthorizedException extends StudyTrackerException {

  public UnauthorizedException() {}

  public UnauthorizedException(String message) {
    super(message);
  }

  public UnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

  public UnauthorizedException(Throwable cause) {
    super(cause);
  }

  public UnauthorizedException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
