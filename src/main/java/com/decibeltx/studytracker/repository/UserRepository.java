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

package com.decibeltx.studytracker.repository;

import com.decibeltx.studytracker.model.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query("{ displayName: { '$regex': ?0, '$options': 'i'  }}")
  List<User> findByDisplayNameLike(String keyword);

  long countByCreatedAtBefore(Date date);

  long countByCreatedAtAfter(Date date);

  long countByCreatedAtBetween(Date startDate, Date endDate);

  long countByActive(boolean active);

}
