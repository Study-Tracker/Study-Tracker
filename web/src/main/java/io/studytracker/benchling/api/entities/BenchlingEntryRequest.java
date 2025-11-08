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

package io.studytracker.benchling.api.entities;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class BenchlingEntryRequest {

  private String folderId;

  private String name;

  private String entryTemplateId;

  private String schemaId;

  private List<String> authorIds;

  private Map<String, CustomField> customFields = new LinkedHashMap<>();

  private Map<String, Field> fields = new LinkedHashMap<>();

  public void addCustomField(String key, String value) {
    customFields.put(key, new CustomField(value));
  }

  public void addField(String key, Object value) {
    fields.put(key, new Field(value));
  }

  @Data
  public static class CustomField {

    private String value;

    public CustomField() {}

    public CustomField(String value) {
      this.value = value;
    }
  }

  @Data
  public static class Field {

    private Object value;

    private Field() {}

    public Field(Object value) {
      this.value = value;
    }
  }
}
