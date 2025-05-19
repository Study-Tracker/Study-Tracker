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

import io.studytracker.export.CompressionUtil;
import io.studytracker.export.DataExportService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/export")
public class DataExportPrivateController {

  @Autowired
  private DataExportService dataExportService;

  @Autowired
  private CompressionUtil compressionUtil;

  @GetMapping("/database")
  public ResponseEntity<Resource> exportDatabase() throws IOException {
    // Export data to CSV files
    Path csvDir = dataExportService.exportAllDataToCsv();

    // Create a ZIP file
    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    Path zipFile = Files.createTempFile("database-export-" + timestamp, ".zip");
    compressionUtil.compressDirectoryToZip(csvDir, zipFile);

    // Clean up the CSV directory
    Files.walk(csvDir)
        .sorted((a, b) -> -a.compareTo(b))
        .forEach(path -> {
          try {
            Files.delete(path);
          } catch (IOException e) {
            // Log error
          }
        });

    // Return the ZIP file as a download
    HttpHeaders headers = new HttpHeaders();
    headers.add(
        HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=database-export-" + timestamp + ".zip");

    Resource resource = new FileSystemResource(zipFile.toFile());
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(zipFile.toFile().length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }
}
