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

package com.decibeltx.studytracker.idbs.inventory.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryResponse {

  @JsonProperty("items")
  private List<InventoryObject> items = new ArrayList<>();

  @JsonProperty("page")
  private Page page;

  @JsonProperty("links")
  private List<Link> links = new ArrayList<>();

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Page {

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("totalElements")
    private Integer totalElements;

    @JsonProperty("totalPages")
    private Integer totalPages;

    @JsonProperty("number")
    private Integer number;

    public boolean hasNext() {
      return totalPages > (number + 1);
    }

  }

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Link {

    @JsonProperty("rel")
    private String rel;

    @JsonProperty("href")
    private String href;

  }

}
