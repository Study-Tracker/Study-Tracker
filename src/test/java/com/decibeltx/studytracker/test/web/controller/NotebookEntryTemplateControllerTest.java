package com.decibeltx.studytracker.test.web.controller;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.decibeltx.studytracker.Application;
import com.decibeltx.studytracker.example.ExampleDataGenerator;
import com.decibeltx.studytracker.exception.RecordNotFoundException;
import com.decibeltx.studytracker.model.NotebookEntryTemplate;
import com.decibeltx.studytracker.model.NotebookEntryTemplate.Category;
import com.decibeltx.studytracker.model.User;
import com.decibeltx.studytracker.repository.NotebookEntryTemplateRepository;
import com.decibeltx.studytracker.repository.UserRepository;
import com.decibeltx.studytracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
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


@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles({"web-test", "example"})
public class NotebookEntryTemplateControllerTest {

    private static final int ENTRY_TEMPLATE_COUNT = ExampleDataGenerator.NOTEBOOK_ENTRY_TEMPLATE_COUNT;

    @Autowired
    private ExampleDataGenerator exampleDataGenerator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotebookEntryTemplateRepository notebookEntryTemplateRepository;

    @Autowired
    private UserService userService;

    private String username;

    @Before
    public void doBefore() {
        exampleDataGenerator.populateDatabase();
        username = userService.findAll().get(0).getUsername();
    }

    @Test
    public void allEntryTemplateTest() throws Exception {
        mockMvc.perform(get("/api/notebookentrytemplate")
            .with(user(username)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(ENTRY_TEMPLATE_COUNT)))
            .andExpect(jsonPath("$[0]", hasKey("id")))
            .andExpect(jsonPath("$[0]", hasKey("name")))
            .andExpect(jsonPath("$[0]", hasKey("templateId")));
    }

    @Test
    public void createEntryTemplateTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        NotebookEntryTemplate notebookEntryTemplate = NotebookEntryTemplate.of(user, "id9999",
            "table3", Category.STUDY, new Date());
        mockMvc.perform(post("/api/notebookentrytemplate")
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(notebookEntryTemplate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$", hasKey("name")))
                .andExpect(jsonPath("$.name", is("table3")))
                .andExpect(jsonPath("$", hasKey("templateId")))
                .andExpect(jsonPath("$.templateId", is("id9999")))
                .andExpect(jsonPath("$", hasKey("category")))
                .andExpect(jsonPath("$.category", is("STUDY")))
                .andExpect(jsonPath("$", hasKey("default")))
                .andExpect(jsonPath("$.default", is(false)))
                .andExpect(jsonPath("$", hasKey("active")))
                .andExpect(jsonPath("$.active", is(true)))
        ;
    }

    @Test
    public void updateEntryTemplateStatusTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        List<NotebookEntryTemplate> templates = notebookEntryTemplateRepository.findAll();
        NotebookEntryTemplate testTemplate = templates.get(0);
        boolean previousStatus = testTemplate.isActive();
        mockMvc.perform(post("/api/notebookentrytemplate/" + testTemplate.getId() + "/status/?active=" + !previousStatus)
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateNonExistentEntryTemplateStatusTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        mockMvc.perform(post("/api/notebookentrytemplate/99999/status/?active=" + false)
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateEntryTemplateTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        List<NotebookEntryTemplate> templates = notebookEntryTemplateRepository.findAll();
        NotebookEntryTemplate testTemplate = templates.get(0);
        testTemplate.setName("updated name");
        testTemplate.setTemplateId("updated id");
        mockMvc.perform(put("/api/notebookentrytemplate/" + testTemplate.getId())
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(testTemplate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$", hasKey("name")))
                .andExpect(jsonPath("$.name", is("updated name")))
                .andExpect(jsonPath("$", hasKey("templateId")))
                .andExpect(jsonPath("$.templateId", is("updated id")));
    }

    @Test
    public void updateNonExistentEntryTemplateTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        List<NotebookEntryTemplate> templates = notebookEntryTemplateRepository.findAll();
        NotebookEntryTemplate testTemplate = templates.get(0);
        testTemplate.setId(999999L);
        mockMvc.perform(put("/api/notebookentrytemplate/" + testTemplate.getId())
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(testTemplate)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getActiveTemplatesTest() throws Exception {
        User user = userRepository.findByUsername("rblack")
                .orElseThrow(RecordNotFoundException::new);
        List<NotebookEntryTemplate> templates = notebookEntryTemplateRepository.findAll().stream()
            .filter(NotebookEntryTemplate::isActive)
            .collect(Collectors.toList());
        int size = templates.size();
        NotebookEntryTemplate testTemplate = templates.get(0);
        mockMvc.perform(post("/api/notebookentrytemplate/" + testTemplate.getId() + "/status/?active=" + false)
                .with(user(user.getUsername()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/notebookentrytemplate/active")
            .with(user(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(size-1)));
    }
}
