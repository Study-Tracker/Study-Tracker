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

package io.studytracker.controller.api.internal;

import io.studytracker.search.GenericSearchHits;
import io.studytracker.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/search")
public class SearchPrivateController {

  @Autowired(required = false)
  private SearchService searchService;

  @GetMapping("")
  public HttpEntity<GenericSearchHits<?>> search(
      @RequestParam("keyword") String keyword,
      @RequestParam(value = "field", required = false) String field) {
    if (searchService != null) {
      GenericSearchHits<?> genericSearchHits;
      if (StringUtils.hasText(field)) {
        genericSearchHits = searchService.search(keyword, field);
      } else {
        genericSearchHits = searchService.search(keyword);
      }
      return new ResponseEntity<>(genericSearchHits, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
  }
}
