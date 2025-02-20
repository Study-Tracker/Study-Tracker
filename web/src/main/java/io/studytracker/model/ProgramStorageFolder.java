/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "program_storage_folders", uniqueConstraints = {
    @UniqueConstraint(name = "uk_program_storage_folder", columnNames = {"program_id", "storage_drive_folder_id"})
})
public class ProgramStorageFolder {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "storage_drive_folder_id", nullable = false, updatable = false)
  private StorageDriveFolder storageDriveFolder;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "program_id", nullable = false, updatable = false)
  private Program program;

  @Column(name = "is_primary", nullable = false)
  private boolean primary = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public StorageDriveFolder getStorageDriveFolder() {
    return storageDriveFolder;
  }

  public void setStorageDriveFolder(StorageDriveFolder storageDriveFolder) {
    this.storageDriveFolder = storageDriveFolder;
  }

  public Program getProgram() {
    return program;
  }

  public void setProgram(Program program) {
    this.program = program;
  }

  public boolean isPrimary() {
    return primary;
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }
}
