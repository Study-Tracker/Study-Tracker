package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okta.commons.lang.Assert;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.mapstruct.dto.api.KeywordCategoryPayloadDto;
import io.studytracker.model.KeywordCategory;
import io.studytracker.repository.KeywordCategoryRepository;
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
public class KeywordCategoryApiControllerTests extends AbstractApiControllerTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private KeywordCategoryRepository keywordCategoryRepository;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/keyword-category")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.KEYWORD_CATEGORY_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.KEYWORD_CATEGORY_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.KEYWORD_CATEGORY_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
  }

  @Test
  public void findByIdTest() throws Exception {
    KeywordCategory category = keywordCategoryRepository.findAll().get(0);
    Assert.notNull(category);
    mockMvc.perform(get("/api/v1/keyword-category/" + category.getId())
        .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(category.getName())));
  }

  @Test
  public void createCategoryTest() throws Exception {
    KeywordCategoryPayloadDto dto = new KeywordCategoryPayloadDto();
    dto.setName("Test Category");
    mockMvc.perform(post("/api/v1/keyword-category")
        .header("Authorization", "Bearer " + this.getToken())
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(dto.getName())));
  }

  @Test
  public void updateCategoryTest() throws Exception {
    KeywordCategory category = keywordCategoryRepository.findAll().get(0);
    Assert.notNull(category);
    KeywordCategoryPayloadDto dto = new KeywordCategoryPayloadDto();
    dto.setId(category.getId());
    dto.setName("Test Category");
    mockMvc.perform(put("/api/v1/keyword-category/" + category.getId())
        .header("Authorization", "Bearer " + this.getToken())
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(dto.getName())));
  }

}
