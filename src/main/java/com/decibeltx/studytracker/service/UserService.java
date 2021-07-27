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

package com.decibeltx.studytracker.service;

import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public List<User> search(String keyword) {
    return userRepository.findByDisplayNameLike(keyword);
  }

  public long count() {
    return userRepository.count();
  }

  @Transactional
  public void create(User user) {
    userRepository.save(user);
  }

  @Transactional
  public void update(User user) {
    User u = userRepository.getOne(user.getId());
    u.setDisplayName(user.getDisplayName());
    u.setEmail(user.getEmail());
    u.setActive(user.isActive());
    u.setTitle(user.getTitle());
    u.setDepartment(user.getDepartment());
    u.setAttributes(user.getAttributes());
    userRepository.save(u);
  }

  @Transactional
  public void updatePassword(User user, String password) {
    User u = userRepository.getOne(user.getId());
    u.setPassword(password);
    u.setCredentialsExpired(false);
    userRepository.save(u);
  }

  @Transactional
  public void delete(User user) {
    userRepository.delete(user);
  }

  public boolean exists(User user) {
    return this.exists(user.getId());
  }

  public boolean exists(Long id) {
    return userRepository.existsById(id);
  }

  public long countFromDate(Date startDate) {
    return userRepository.countByCreatedAtAfter(startDate);
  }

  public long countBeforeDate(Date endDate) {
    return userRepository.countByCreatedAtBefore(endDate);
  }

  public long countBetweenDates(Date startDate, Date endDate) {
    return userRepository.countByCreatedAtBetween(startDate, endDate);
  }

  public long countActiveUsers() {
    return userRepository.countByActive(true);
  }

}
