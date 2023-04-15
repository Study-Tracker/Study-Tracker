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

package io.studytracker.repository;

import io.studytracker.model.AwsIntegration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AwsIntegrationRepository extends JpaRepository<AwsIntegration, Long> {

  @Query("SELECT a FROM AwsIntegration a WHERE a.organization.id = ?1")
  List<AwsIntegration> findByOrganizationId(Long organizationId);

  @Query("SELECT a FROM AwsIntegration a JOIN S3Bucket b ON a.id = b.awsIntegration.id WHERE b.id = ?1")
  AwsIntegration findByBucketDriveId(Long bucketDriveId);

  @Query("SELECT a FROM AwsIntegration a "
      + " JOIN S3Bucket b ON b.awsIntegration.id = a.id "
      + " JOIN S3BucketFolder f ON f.s3Bucket.id = b.id "
      + " WHERE f.id = ?1")
  AwsIntegration findByBucketFolderId(Long folderId);

  @Query("SELECT a FROM AwsIntegration a "
      + " JOIN S3Bucket b ON a.id = b.awsIntegration.id "
      + " JOIN StorageDrive d ON b.storageDrive.id = d.id "
      + " JOIN StorageDriveFolder df ON d.id = df.storageDrive.id "
      + " WHERE df.id = ?1")
  Optional<AwsIntegration> findByStorageDriveFolderId(Long folderId);

  @Query("SELECT a FROM AwsIntegration a "
      + " JOIN S3Bucket b ON b.awsIntegration.id = a.id "
      + " JOIN StorageDrive d ON b.storageDrive.id = d.id "
      + " WHERE d.id = ?1")
  Optional<AwsIntegration> findByStorageDriveId(Long driveId);

}
