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

package com.decibeltx.studytracker.teams.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.Date;
import lombok.Data;

/**
 * "id": "string (identifier)", "createdBy": {"@odata.type": "microsoft.graph.identitySet"},
 * "createdDateTime": "String (timestamp)", "eTag": "string", "lastModifiedBy": {"@odata.type":
 * "microsoft.graph.identitySet"}, "lastModifiedDateTime": "String (timestamp)", "name": "string",
 * "parentReference": {"@odata.type": "microsoft.graph.itemReference"}, "webUrl": "string",
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseItem {

  @JsonProperty("id")
  private String id;

  @JsonProperty("createdBy")
  private IdentitySet createdBy;

  @JsonProperty("createdDateTime")
  private Date createdDate;

  @JsonProperty("eTag")
  private String eTag;

  @JsonProperty("lastModifiedBy")
  private IdentitySet lastModifiedBy;

  @JsonProperty("lastModifiedDateTime")
  private Date lastModifiedDate;

  @JsonProperty("name")
  private String name;

  @JsonProperty("parentReference")
  private ItemReference parentReference;

  @JsonProperty("webUrl")
  private URL webUrl;

}
