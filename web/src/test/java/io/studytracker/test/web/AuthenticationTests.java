/*
 * Copyright 2022 the original author or authors.
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

package io.studytracker.test.web;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.model.Program;
import io.studytracker.model.Status;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import io.studytracker.repository.PasswordResetTokenRepository;
import io.studytracker.repository.ProgramRepository;
import io.studytracker.repository.UserRepository;
import io.studytracker.security.AuthCredentials;
import io.studytracker.service.EmailService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class AuthenticationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private Environment env;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ProgramRepository programRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired private ExampleDataGenerator exampleDataGenerator;

  @Autowired private PasswordEncoder passwordEncoder;

  @MockBean private EmailService emailService;

  @Value("${security.example.user}")
  private String exampleUser;

  @Value("${security.example.password}")
  private String examplePassword;

  @Before
  public void doBefore() {
    exampleDataGenerator.populateDatabase();
    String email = env.getRequiredProperty("security.example.user");
    Optional<User> optional = userRepository.findByEmail(email);
    if (!optional.isPresent()) {
      User user = new User();
      user.setPassword(
          passwordEncoder.encode(env.getRequiredProperty("security.example.password")));
      user.setDisplayName(email);
      user.setActive(true);
      user.setEmail(email);
      userRepository.save(user);
    }
  }

  @Test
  public void accessOpenEndpointTest() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/internal/study/"))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
  }

  @Test
  public void tokenGenerationTest() throws Exception {
    AuthCredentials authCredentials = new AuthCredentials();
    authCredentials.setUsername(exampleUser);
    authCredentials.setPassword(examplePassword);
    mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsBytes(authCredentials)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(jsonPath("$", hasKey("token")));
  }

  @Test
  public void postWithoutAuthenticationTest() throws Exception {
    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/internal/study")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
  }

  @Test
  public void postWithAuthenticationTest() throws Exception {

    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);
    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);

    Study study = new Study();
    study.setStatus(Status.ACTIVE);
    study.setName("New Study X");
    study.setProgram(program);
    study.setDescription("This is a test");
    study.setLegacy(false);
    study.setCreatedBy(user);
    study.setLastModifiedBy(user);
    study.setStartDate(new Date());
    study.setOwner(user);
    study.setUsers(Collections.singleton(user));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/internal/study")
                .with(user(user.getEmail()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(study)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void passwordResetRequestTest() throws Exception {

    Mockito.doNothing()
        .when(emailService)
        .sendPasswordResetEmail(Mockito.anyString(), Mockito.anyString());

    User user = userRepository.findByEmail("jsmith@email.com").orElseThrow(RecordNotFoundException::new);
    Assert.assertEquals(0, passwordResetTokenRepository.findByUserId(user.getId()).size());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/auth/passwordresetrequest")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(
                    EntityUtils.toString(
                        new UrlEncodedFormEntity(
                            Arrays.asList(new BasicNameValuePair("email", user.getEmail()))))))
        .andExpect(MockMvcResultMatchers.status().isFound());

    Assert.assertEquals(1, passwordResetTokenRepository.findByUserId(user.getId()).size());
  }
}
