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

package io.studytracker.eln;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.studytracker.model.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class NotebookFolder extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "url", nullable = false, length = 1024)
  private String url;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", length = 1024)
  private String path;

  @Column(name = "reference_id")
  private String referenceId;

  @Transient
  @JsonSerialize
  private Map<String, Object> attributes = new HashMap<>();

  @Transient
  @JsonSerialize
  private NotebookFolder parentFolder;

  @Transient
  @JsonSerialize
  private List<? extends NotebookFolder> subFolders = new ArrayList<>();

  @Transient
  @JsonSerialize
  private List<NotebookEntry> entries = new ArrayList<>();

}
