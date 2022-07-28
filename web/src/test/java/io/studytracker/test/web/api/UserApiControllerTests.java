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

package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.mapstruct.dto.api.UserPayloadDto;
import io.studytracker.mapstruct.mapper.UserMapper;
import io.studytracker.model.User;
import io.studytracker.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class UserApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserMapper userMapper;

  @Test
  public void unauthorizedFetchTest() throws Exception {
    mockMvc
        .perform(get("/api/v1/user"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());
  }

  /*
  {
    "content":[
      {"id":161,"department":"Biology","title":"Director","displayName":"Joe Smith","email":"jsmith@email.com","admin":false,"createdAt":1658939207956,"updatedAt":1658939207956,"attributes":{},"active":true,"locked":false,"expired":false,"credentialsExpired":false,"configuration":{}},
      {"id":162,"department":"Biology","title":"Sr. Scientist","displayName":"Ann Johnson","email":"ajohnson@email.com","admin":false,"createdAt":1658939207957,"updatedAt":1658939207957,"attributes":{},"active":true,"locked":false,"expired":false,"credentialsExpired":false,"configuration":{}},
      {"id":163,"department":"IT","title":"IT Admin","displayName":"Rob Black","email":"rblack@email.com","admin":true,"createdAt":1658939207957,"updatedAt":1658939207957,"attributes":{},"active":true,"locked":false,"expired":false,"credentialsExpired":false,"configuration":{}}
    ],
    "pageable":{
      "sort":{
        "empty":true,
        "sorted":false,
        "unsorted":true
      },
      "offset":0,
      "pageNumber":0,
      "pageSize":100,
      "paged":true,
      "unpaged":false
    },
    "last":true,
    "totalPages":1,
    "totalElements":3,
    "first":true,
    "size":100,
    "number":0,
    "sort":{"empty":true,"sorted":false,"unsorted":true},
    "numberOfElements":3,
    "empty":false
  }
   */

  @Test
  public void findAllTest() throws Exception {
    mockMvc
        .perform(get("/api/v1/user")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(3)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(3)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(3)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findUserByIdTest() throws Exception {

    User user = new User();
    user.setEmail("newperson@email.com");
    user.setDisplayName("New Person");
    user.setDepartment("IT");
    user.setTitle("IT Admin");
    user.setAdmin(true);
    userService.create(user);
    Assert.assertNotNull(user.getId());

    mockMvc
        .perform(
            get("/api/v1/user/" + user.getId())
                .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("email")))
        .andExpect(jsonPath("$.email", is("newperson@email.com")));
  }

  @Test
  public void createUserTest() throws Exception {

    UserPayloadDto dto = new UserPayloadDto();
    dto.setEmail("newperson@email.com");
    dto.setDepartment("IT");
    dto.setTitle("IT Admin");
    dto.setAdmin(true);

    mockMvc.perform(post("/api/v1/user")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isBadRequest());

    dto.setDisplayName("New Person");

    mockMvc.perform(post("/api/v1/user")
        .header("Authorization", "Bearer " + this.getToken())
        .contentType("application/json")
        .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("email")))
        .andExpect(jsonPath("$.email", is("newperson@email.com")));

  }

  @Test
  public void updateUserTest() throws Exception {

    User user = new User();
    user.setEmail("newperson@email.com");
    user.setDisplayName("New Person");
    user.setDepartment("IT");
    user.setTitle("IT Admin");
    user.setAdmin(true);
    userService.create(user);
    Assert.assertNotNull(user.getId());

    UserPayloadDto dto = userMapper.toUserPayload(user);
    dto.setTitle("Director of IT");

    mockMvc
        .perform(
            put("/api/v1/user/" + user.getId())
                .header("Authorization", "Bearer " + this.getToken())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("title")))
        .andExpect(jsonPath("$.title", is("Director of IT")));
    }

}
