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

package io.studytracker.benchling.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BenchlingCustomEntity {

  private String id;
  private String name;
  private String registryId;
  private String entityRegistryId;
  private List<String> aliases = new ArrayList<>();
  private String apiURL;
  private String webURL;
  private BenchlingArchiveRecord archiveRecord;
  private Map<String, Object> customFields = new LinkedHashMap<>();
  private Map<String, BenchlingCustomEntityField> fields = new LinkedHashMap<>();
  private String folderId;
  private BenchlingCustomEntitySchema schema;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BenchlingCustomEntityField {
    private String displayValue;
    private @JsonProperty(value = "isMulti") boolean isMulti;
    private String type;
    private String textValue;
    private Object value;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BenchlingCustomEntitySchema {
    private String id;
    private String name;
  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BenchlingCustomEntityList {
    private List<BenchlingCustomEntity> customEntities = new ArrayList<>();
    private String nextToken;
  }

}
