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

package io.studytracker.mapstruct.mapper;

import io.studytracker.mapstruct.dto.form.S3BucketFormDto;
import io.studytracker.mapstruct.dto.response.S3BucketDetailsDto;
import io.studytracker.model.S3Bucket;
import java.util.List;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface S3BucketMapper {

  S3Bucket fromFormDto(S3BucketFormDto dto);

  S3BucketDetailsDto toDto(S3Bucket s3Bucket);
  List<S3BucketDetailsDto> toDto(List<S3Bucket> s3Buckets);
  Set<S3BucketDetailsDto> toDto(Set<S3Bucket> s3Buckets);

}
