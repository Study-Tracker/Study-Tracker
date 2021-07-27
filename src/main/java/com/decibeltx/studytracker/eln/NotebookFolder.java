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

package com.decibeltx.studytracker.eln;

import com.decibeltx.studytracker.model.ELNFolder;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
public class NotebookFolder {

  @Transient
  private NotebookFolder parentFolder;

  @NotNull
  private String url;

  @NotNull
  private String name;

  @NotNull
  private String path;

  @NotNull
  private String referenceId;

  @Transient
  private List<NotebookFolder> subFolders = new ArrayList<>();

  @Transient
  private List<NotebookEntry> entries = new ArrayList<>();

  private Map<String, Object> attributes = new HashMap<>();

  public static NotebookFolder from(ELNFolder folder) {
    NotebookFolder f = new NotebookFolder();
    f.setName(folder.getName());
    f.setPath(folder.getPath());
    f.setUrl(folder.getUrl());
    f.setReferenceId(folder.getReferenceId());
    return f;
  }

  public void setUrl(URL url) {
    this.url = url.toString();
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
