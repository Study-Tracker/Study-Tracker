package com.decibeltx.studytracker.web.example;

import com.decibeltx.studytracker.config.UserRepositoryPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UserPopulatorRunner implements ApplicationRunner {

  @Autowired(required = false)
  private UserRepositoryPopulator userRepositoryPopulator;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (userRepositoryPopulator != null) {
      userRepositoryPopulator.populateUserRepository();
    }
  }
}
