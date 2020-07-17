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

package com.decibeltx.studytracker.benchling.eln.entities;

import com.decibeltx.studytracker.core.notebook.SimpleNotebookEntry;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingNotebookEntry extends SimpleNotebookEntry {


  private String folderId;





  private String entityId;
  private String visibileUniqueId;
  private String viewableInEntityId;
  private String entityTypeName;
  private String nodeDisplayText;
  private String entityName;

  @SuppressWarnings("PMD")
  @JsonProperty("entityCore")
  private void upackNested(Map<String, Object> map) {
    if (map.containsKey("entityId")) {
      entityId = (String) map.get("entityId");
    }
  /*  if (map.containsKey("visibileUniqueId")) {
      visibileUniqueId = (String) map.get("visibileUniqueId");
    }
    if (map.containsKey("viewableInEntityId")) {
      viewableInEntityId = (String) map.get("viewableInEntityId");
    }
    if (map.containsKey("entityTypeName")) {
      entityTypeName = (String) map.get("entityTypeName");
    }
    if (map.containsKey("nodeDisplayText")) {
      nodeDisplayText = (String) map.get("nodeDisplayText");
    }
    if (map.containsKey("entityName")) {
      entityName = (String) map.get("entityName");
    }*/
  }

  public String getFolderId() {
    return folderId;
  }

  public void setFolderId(String folderId) {
    this.folderId = folderId;
  }



}
