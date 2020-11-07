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

import com.decibeltx.studytracker.core.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

  Optional<User> findById(String id);

  List<User> findAll();

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  List<User> search(String keyword);

  long count();

  void create(User user);

  void update(User user);

  void delete(User user);

}
