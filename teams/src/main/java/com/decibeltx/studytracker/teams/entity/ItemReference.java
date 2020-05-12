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
import lombok.Data;

/**
 * { "driveId": "string", "driveType": "personal | business | documentLibrary", "id": "string",
 * "name": "string", "path": "string", "shareId": "string", "sharepointIds": { "@odata.type":
 * "microsoft.graph.sharepointIds" } }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemReference {

  private String driveId;
  private String driveType;
  private String id;
  private String name;
  private String path;
  private String shareId;
  private Object sharepointIds;

}
