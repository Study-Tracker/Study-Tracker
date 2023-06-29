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


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = S3FolderDetails.class, name = S3FolderDetails.DISCRIMINATOR),
    @JsonSubTypes.Type(value = LocalDriveFolderDetails.class, name = LocalDriveFolderDetails.DISCRIMINATOR),
    @JsonSubTypes.Type(value = EgnyteFolderDetails.class, name = EgnyteFolderDetails.DISCRIMINATOR),
    @JsonSubTypes.Type(value = OneDriveFolderDetails.class, name = OneDriveFolderDetails.DISCRIMINATOR)
})
@EqualsAndHashCode
public abstract class StorageDriveFolderDetails {

  abstract String getWebUrl();

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
  public abstract String getType();


}
