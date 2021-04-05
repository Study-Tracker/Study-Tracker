package com.decibeltx.studytracker.config;

/**
 * Helper interface for a class which populates the {@link com.decibeltx.studytracker.repository.UserRepository}
 * with all available user records.
 */
public interface UserRepositoryPopulator {

  void populateUserRepository();

}
