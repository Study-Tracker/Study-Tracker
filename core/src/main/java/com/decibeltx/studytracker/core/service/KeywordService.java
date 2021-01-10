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

package com.decibeltx.studytracker.core.service;

import com.decibeltx.studytracker.core.model.Keyword;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface KeywordService {

  Optional<Keyword> findById(String id);

  List<Keyword> findAll();

  List<Keyword> findByKeyword(String keyword);

  List<Keyword> findByCategory(String category);

  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

  List<Keyword> search(String fragment);

  List<Keyword> search(String fragment, String category);

  Set<String> findAllCategories();

  Keyword create(Keyword keyword);

  Keyword update(Keyword keyword);

  void delete(Keyword keyword);

}
