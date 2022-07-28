package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.ProgramPayloadDto;
import io.studytracker.model.Program;
import io.studytracker.model.User;
import io.studytracker.repository.ProgramRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class ProgramApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProgramRepository programRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/program")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.PROGRAM_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.PROGRAM_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.PROGRAM_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
    ;
  }

  @Test
  public void findProgramById() throws Exception {

    Program program =
        programRepository
            .findByName("Clinical Program A")
            .orElseThrow(RecordNotFoundException::new);

    mockMvc
        .perform(get("/api/v1/program/" + program.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("code")))
        .andExpect(jsonPath("$.code", is("CPA")))
        .andExpect(jsonPath("$", hasKey("active")))
        .andExpect(jsonPath("$.active", is(true)))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Clinical Program A")))
        .andExpect(jsonPath("$", hasKey("createdBy")))
        .andExpect(jsonPath("$.createdBy", is(program.getCreatedBy().getId().intValue())));
  }

  @Test
  public void findNonExistantProgramTest() throws Exception {
    mockMvc
        .perform(get("/api/v1/program/999999")
            .header("Authorization", "Bearer " + this.getToken()))
        .andExpect(status().isNotFound());
  }

  @Test
  public void createProgramTest() throws Exception {

    User user = this.getUserRepository().findAll().stream()
        .filter(u -> !u.isAdmin())
        .findFirst()
        .get();

    ProgramPayloadDto dto = new ProgramPayloadDto();
    dto.setName("Program X");
    dto.setCode("PX");
    dto.setActive(true);

    mockMvc
        .perform(
            post("/api/v1/program/")
                .header("Authorization",
                    "Bearer " + this.getTokenUtils().generateToken(user.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andExpect(status().isForbidden());

    mockMvc
        .perform(
            post("/api/v1/program/")
                .header("Authorization", "Bearer " + this.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("Program X")))
        .andExpect(jsonPath("$", hasKey("createdBy")))
        .andExpect(jsonPath("$.createdBy", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("lastModifiedBy")))
        .andExpect(jsonPath("$.lastModifiedBy", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("createdAt")))
        .andExpect(jsonPath("$.createdAt", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("updatedAt")))
        .andExpect(jsonPath("$.updatedAt", not(nullValue())));

    Program program = programRepository.findByName("Program X")
        .orElseThrow(RecordNotFoundException::new);
    Assert.assertNotNull(program.getStorageFolder());
    System.out.println(program.getStorageFolder().getPath());
    System.out.println(program.getStorageFolder().getName());
    System.out.println(program.getStorageFolder().getId());

  }

}
