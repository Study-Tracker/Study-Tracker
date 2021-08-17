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

import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.PasswordResetToken;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.PasswordResetTokenRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

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

  // Password reset

  public PasswordResetToken createPasswordResetToken(User user, int days) {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, days);

    PasswordResetToken token = new PasswordResetToken();
    token.setToken(UUID.randomUUID().toString());
    token.setUser(user);
    token.setExpirationDate(calendar.getTime());

    return passwordResetTokenRepository.save(token);

  }

  public PasswordResetToken createPasswordResetToken(User user) {
    return createPasswordResetToken(user, 1);
  }

  public boolean validatePasswordResetToken(String email, String token) {
    Optional<PasswordResetToken> optional = passwordResetTokenRepository.findByToken(token);
    if (optional.isPresent()) {
      PasswordResetToken resetToken = optional.get();
      final Calendar cal = Calendar.getInstance();
      if (!resetToken.getUser().getEmail().equals(email)) {
        LOGGER.warn("Supplied email address does not match token owner.");
        return false;
      } else if (resetToken.getExpirationDate().before(cal.getTime())) {
        LOGGER.warn("Token has expired.");
        return false;
      } else {
        return true;
      }
    } else {
      LOGGER.warn("Token not found: " + token);
      return false;
    }
  }

  @Transactional
  public void deletePasswordResetToken(String token) {
    PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
        .orElseThrow(() -> new RecordNotFoundException("Cannot find password reset token: " + token));
    passwordResetTokenRepository.deleteById(passwordResetToken.getId());
  }

}
