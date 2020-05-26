package com.decibeltx.studytracker.core.config;

/**
 * Helper interface for a class which populates the {@link com.decibeltx.studytracker.core.repository.UserRepository}
 * with all available user records.
 */
public interface UserRepositoryPopulator {

  void populateUserRepository();

}
