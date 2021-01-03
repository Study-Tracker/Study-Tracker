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

package com.decibeltx.studytracker.core.model;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "keywords")
public class Keyword {

  @Id
  private String id;

  @NotNull
  private String keyword;

  private String category;

  public Keyword() {
  }

  public Keyword(@NotNull String keyword, String category) {
    this.keyword = keyword;
    this.category = category;
  }

  public Keyword(String id, @NotNull String keyword, String category) {
    this.id = id;
    this.keyword = keyword;
    this.category = category;
  }
}
