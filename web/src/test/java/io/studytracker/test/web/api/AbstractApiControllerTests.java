package io.studytracker.test.web.api;

import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.model.User;
import io.studytracker.repository.UserRepository;
import io.studytracker.security.TokenUtils;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractApiControllerTests {

  private TokenUtils tokenUtils;

  private ExampleDataGenerator exampleDataGenerator;

  private UserRepository userRepository;

  private String token;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    if (token == null) {
      String email = userRepository.findAll().stream()
          .filter(User::isAdmin)
          .findFirst()
          .get()
          .getEmail();
      token = tokenUtils.generateToken(email);
    }
  }

  public TokenUtils getTokenUtils() {
    return tokenUtils;
  }

  @Autowired
  public void setTokenUtils(TokenUtils tokenUtils) {
    this.tokenUtils = tokenUtils;
  }

  public ExampleDataGenerator getExampleDataGenerator() {
    return exampleDataGenerator;
  }

  @Autowired
  public void setExampleDataGenerator(ExampleDataGenerator exampleDataGenerator) {
    this.exampleDataGenerator = exampleDataGenerator;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  @Autowired
  public void setUserRepository(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
