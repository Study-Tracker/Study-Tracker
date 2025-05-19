/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.studytracker.controller.api.internal;

import io.studytracker.controller.api.AbstractApiController;
import io.studytracker.exception.InsufficientPrivilegesException;
import io.studytracker.export.DataExportService;
import io.studytracker.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/internal/export")
public class ExportPrivateController extends AbstractApiController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExportPrivateController.class);

  @Autowired
  private DataExportService dataExportService;

  /**
   * Triggers an asynchronous export of all database records to CSV files.
   * Only admin users are allowed to access this endpoint.
   *
   * @return a response with a job ID that can be used to check the status of the export
   */
  @GetMapping("")
  public ResponseEntity<Map<String, Object>> exportDatabase() {
    LOGGER.info("Received request to export database");

    // Check if the user is an admin
    User user = getAuthenticatedUser();
    if (!user.isAdmin()) {
      LOGGER.warn("Non-admin user {} attempted to export database", user.getUsername());
      throw new InsufficientPrivilegesException("Only administrators can export the database");
    }

    // Generate a unique job ID
    String jobId = UUID.randomUUID().toString();
    LOGGER.info("Starting database export job with ID: {}", jobId);

    // Start the export process asynchronously
    startExportAsync(jobId);

    // Return a response with the job ID
    Map<String, Object> response = new HashMap<>();
    response.put("jobId", jobId);
    response.put("status", "STARTED");
    response.put("message", "Database export started. The process will run in the background.");

    return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
  }

  /**
   * Starts the export process asynchronously.
   *
   * @param jobId the unique identifier for this export job
   * @return a CompletableFuture that will be completed when the export is done
   */
  @Async("taskExecutor")
  public CompletableFuture<Path> startExportAsync(String jobId) {
    LOGGER.info("Starting asynchronous export with job ID: {}", jobId);
    try {
      Path exportPath = dataExportService.exportAllDataToCsv();
      LOGGER.info("Export completed successfully. Files available at: {}", exportPath);
      return CompletableFuture.completedFuture(exportPath);
    } catch (IOException e) {
      LOGGER.error("Error during database export: {}", e.getMessage(), e);
      return CompletableFuture.failedFuture(e);
    }
  }
}
