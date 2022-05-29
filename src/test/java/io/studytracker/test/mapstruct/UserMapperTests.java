package io.studytracker.test.mapstruct;

import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.mapstruct.dto.UserSummaryDto;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.model.User;
import io.studytracker.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "example"})
public class UserMapperTests {

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private UserRepository userRepository;

  @Autowired private UserMapper userMapper;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
  }

  @Test
  public void userMappingTest() {

    List<User> users = userRepository.findAll();
    Assert.assertFalse(users.isEmpty());
    List<UserSummaryDto> dtos =
        users.stream().map(userMapper::toUserSummary).collect(Collectors.toList());
    Assert.assertNotNull(dtos);
    Assert.assertFalse(dtos.isEmpty());
    System.out.println(dtos);
  }
}
