package io.studytracker.test.web.api;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.studytracker.Application;
import io.studytracker.example.ExampleDataGenerator;
import io.studytracker.exception.RecordNotFoundException;
import io.studytracker.mapstruct.dto.api.StudyCollectionPayloadDto;
import io.studytracker.model.Study;
import io.studytracker.model.StudyCollection;
import io.studytracker.repository.StudyCollectionRepository;
import io.studytracker.repository.StudyRepository;
import io.studytracker.repository.UserRepository;
import java.util.Set;
import java.util.stream.Collectors;
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
public class StudyCollectionApiControllerTests extends AbstractApiControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudyRepository studyRepository;

  @Autowired
  private StudyCollectionRepository studyCollectionRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findAllTest() throws Exception {
    mockMvc.perform(get("/api/v1/study-collection")
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("content")))
        .andExpect(jsonPath("$.content", not(empty())))
        .andExpect(jsonPath("$.content", hasSize(ExampleDataGenerator.STUDY_COLLECTION_COUNT)))
        .andExpect(jsonPath("$", hasKey("last")))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$", hasKey("pageable")))
        .andExpect(jsonPath("$.pageable", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("totalPages")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$", hasKey("totalElements")))
        .andExpect(jsonPath("$.totalElements", is(ExampleDataGenerator.STUDY_COLLECTION_COUNT)))
        .andExpect(jsonPath("$", hasKey("first")))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$", hasKey("size")))
        .andExpect(jsonPath("$.size", is(100)))
        .andExpect(jsonPath("$", hasKey("number")))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$", hasKey("numberOfElements")))
        .andExpect(jsonPath("$.numberOfElements", is(ExampleDataGenerator.STUDY_COLLECTION_COUNT)))
        .andExpect(jsonPath("$", hasKey("sort")))
        .andExpect(jsonPath("$", hasKey("empty")))
        .andExpect(jsonPath("$.empty", is(false)));
    ;

  }

  @Test
  public void findByIdTest() throws Exception {

    StudyCollection collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .get();

    mockMvc.perform(get("/api/v1/study-collection/" + collection.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(collection.getName())))
        .andExpect(jsonPath("$", hasKey("description")))
        .andExpect(jsonPath("$.description", is(collection.getDescription())))
        .andExpect(jsonPath("$", hasKey("studies")))
        .andExpect(jsonPath("$.studies", hasSize(2)))
        ;

  }

  @Test
  public void createTest() throws Exception {
    Study study = studyRepository.findByCode("CPA-10001")
        .orElseThrow(RecordNotFoundException::new);
    StudyCollectionPayloadDto dto = new StudyCollectionPayloadDto();
    dto.setName("New collection");
    dto.setDescription("This is a test");
    dto.setShared(true);
    dto.setStudies(Set.of(study.getId()));

    mockMvc.perform(post("/api/v1/study-collection")
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is("New collection")))
        .andExpect(jsonPath("$", hasKey("studies")))
        .andExpect(jsonPath("$.studies", hasSize(1)))
        .andExpect(jsonPath("$.studies[0]", is(study.getId().intValue())))
    ;

  }

  @Test
  public void updateTest() throws Exception {
    StudyCollection collection = studyCollectionRepository.findAll().stream()
            .filter(c -> c.getName().equals("Example public collection"))
            .findFirst()
            .map(c -> studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new))
            .get();
    Study study = studyRepository.findByCode("CPA-10002").orElseThrow(RecordNotFoundException::new);
    Set<Study> studies = collection.getStudies();
    studies.add(study);
    StudyCollectionPayloadDto dto = new StudyCollectionPayloadDto();
    dto.setId(collection.getId());
    dto.setName(collection.getName());
    dto.setDescription(collection.getDescription());
    dto.setStudies(studies.stream().map(Study::getId).collect(Collectors.toSet()));

    mockMvc.perform(put("/api/v1/study-collection/" + collection.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasKey("id")))
        .andExpect(jsonPath("$.id", not(nullValue())))
        .andExpect(jsonPath("$", hasKey("name")))
        .andExpect(jsonPath("$.name", is(dto.getName())))
        .andExpect(jsonPath("$", hasKey("studies")))
        .andExpect(jsonPath("$.studies", hasSize(3)));

  }

  @Test
  public void deleteTest() throws Exception {
    StudyCollection collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .get();

    mockMvc.perform(delete("/api/v1/study-collection/" + collection.getId())
            .header("Authorization", "Bearer " + this.getToken()))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    Assert.assertEquals(1, studyCollectionRepository.findAll().size());
  }

  @Test
  public void addToCollectionTest() throws Exception {
    StudyCollection collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .map(c -> studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new))
        .get();
    Assert.assertEquals(collection.getStudies().size(), 2);
    Study study = studyRepository.findByCode("CPA-10002").orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(post("/api/v1/study-collection/" + collection.getId() + "/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .map(c -> studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new))
        .get();
    Assert.assertEquals(collection.getStudies().size(), 3);

  }

  @Test
  public void removeStudyTest() throws Exception {
    StudyCollection collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .map(c -> studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new))
        .get();
    Assert.assertEquals(collection.getStudies().size(), 2);
    Study study = studyRepository.findByCode("CPA-10001").orElseThrow(RecordNotFoundException::new);

    mockMvc.perform(delete("/api/v1/study-collection/" + collection.getId() + "/" + study.getId())
            .header("Authorization", "Bearer " + this.getToken())
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());

    collection = studyCollectionRepository.findAll().stream()
        .filter(c -> c.getName().equals("Example public collection"))
        .findFirst()
        .map(c -> studyCollectionRepository.findById(c.getId()).orElseThrow(RecordNotFoundException::new))
        .get();
    Assert.assertEquals(collection.getStudies().size(), 1);
  }

}
