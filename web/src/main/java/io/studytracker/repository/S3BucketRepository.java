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

import io.studytracker.model.S3Bucket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface S3BucketRepository extends StorageDriveDetailsOperations<S3Bucket> {

  @Query("SELECT b FROM S3Bucket b WHERE b.awsIntegration.id = ?1 and b.name = ?2")
  Optional<S3Bucket> findByIntegrationAndName(Long integrationId, String name);

  @Query("SELECT b FROM S3Bucket b JOIN S3BucketFolder bf ON b.id = bf.s3Bucket.id WHERE bf.storageDriveFolder.id = ?1")
  Optional<S3Bucket> findByStorageDriveFolderId(Long storageDriveFolderId);

  @Query("SELECT b FROM S3Bucket b WHERE b.awsIntegration.id = ?1")
  List<S3Bucket> findByAwsIntegrationId(Long integrationId);

  @Override
  @Query("SELECT b FROM S3Bucket b WHERE b.storageDrive.id = ?1")
  Optional<S3Bucket> findByStorageDriveId(Long storageDriveId);

  @Override
  @Query("SELECT b FROM S3Bucket b WHERE b.awsIntegration.id = ?1")
  List<S3Bucket> findByIntegrationId(Long integrationId);

  @Override
  @Query("SELECT b FROM S3Bucket b JOIN AwsIntegration i ON b.awsIntegration.id = i.id WHERE i.organization.id = ?1")
  List<S3Bucket> findByOrganizationId(Long organizationId);

}
