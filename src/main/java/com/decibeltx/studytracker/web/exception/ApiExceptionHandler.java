/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.web.exception;

import com.decibeltx.studytracker.exception.DuplicateRecordException;
import com.decibeltx.studytracker.exception.InvalidConstraintException;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(basePackages = {"com.decibeltx.studytracker.web.controller.api"})
@RestController
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    ex.printStackTrace();
    ApiError apiError = new ApiError("Validation failed", ex.getBindingResult().toString());
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public final ResponseEntity<ApiError> unauthroizedError(UnauthorizedException ex,
      WebRequest webRequest) {
    ex.printStackTrace();
    ApiError apiError = new ApiError(ex.getMessage(), webRequest.getDescription(false));
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InvalidConstraintException.class)
  public final ResponseEntity<ApiError> invalidConstraint(InvalidConstraintException ex,
      WebRequest request) {
    ex.printStackTrace();
    ApiError apiError = new ApiError(ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RecordNotFoundException.class)
  public final ResponseEntity<ApiError> recordNotFound(RecordNotFoundException ex,
      WebRequest request) {
    ex.printStackTrace();
    ApiError apiError = new ApiError(ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateRecordException.class)
  public final ResponseEntity<ApiError> duplicateRecord(DuplicateRecordException ex,
      WebRequest request) {
    ex.printStackTrace();
    ApiError apiError = new ApiError(ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public final ResponseEntity<ApiError> genericException(Exception ex, WebRequest request) {
    ex.printStackTrace();
    ApiError apiError = new ApiError(ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
