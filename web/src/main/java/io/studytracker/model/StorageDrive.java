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

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

@Entity
@Table(name = "storage_drives")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class StorageDrive {

  public enum DriveType {
    LOCAL, S3, EGNYTE, ONEDRIVE
  }

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "hibernate_sequence"
  )
  @SequenceGenerator(
      name = "hibernate_sequence",
      allocationSize = 1
  )
  private Long id;

  @Column(name = "display_name", nullable = false, unique = true)
  private String displayName;

  @Column(name = "drive_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private DriveType driveType;

  @Column(name = "root_path", nullable = false)
  private String rootPath;

  @Column(name = "active", nullable = false)
  private boolean active;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @Type(JsonBinaryType.class)
  @Column(name = "details", columnDefinition = "json")
  private StorageDriveDetails details;

  public void setDetails(StorageDriveDetails details) {
    Assert.isTrue(StorageDriveDetails.class.isAssignableFrom(details.getClass()),
        "Details type must extend StorageDriveDetails, but was: " + details.getClass().getName());
    this.details = details;
  }
}
