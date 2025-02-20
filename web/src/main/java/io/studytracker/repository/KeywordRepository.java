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

package io.studytracker.repository;

import io.studytracker.model.Keyword;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

  List<Keyword> findByKeyword(String keyword);

  @Query("select k from Keyword k where k.category = ?1")
  List<Keyword> findByCategory(String category);

  @Query("select k from Keyword k where k.keyword = ?1 and k.category = ?2")
  Optional<Keyword> findByKeywordAndCategory(String keyword, String category);

  @Query("select distinct(k.category) from Keyword k")
  Set<String> findAllCategories();

  @Query(value = "select k.* from keywords k join study_keywords sk on k.id = sk.keyword_id where sk.study_id = ?1",
      nativeQuery = true)
  List<Keyword> findByStudyId(Long studyId);

  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment, Pageable pageable);

  @Query(
      "select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category = ?2")
  List<Keyword> search(String fragment, String category, Pageable pageable);

  @Query("select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%'))")
  List<Keyword> search(String fragment);

  @Query(
      "select k from Keyword k where lower(k.keyword) like lower(concat('%', ?1, '%')) and k.category = ?2")
  List<Keyword> search(String fragment, String category);

}
