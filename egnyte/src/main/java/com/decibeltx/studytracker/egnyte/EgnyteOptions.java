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

package com.decibeltx.studytracker.egnyte;

import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.util.Assert;

@Data
public class EgnyteOptions {

  private URL rootUrl;
  private String rootPath;
  private String token;
  private Integer qps = 1;
  private Integer sleep;
  private Integer maxReadDepth = 1;


  @PostConstruct
  void init() {
    Assert.notNull(rootUrl, "Root URL must not be null.");
    Assert.notNull(rootPath, "Root path must not be null.");
    Assert.notNull(token, "Authentication token must not be null.");
    sleep = 1000 / qps + 10;
    if (!rootPath.endsWith("/")) {
      rootPath = rootPath + "/";
    }
  }

}
