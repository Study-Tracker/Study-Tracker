package io.studytracker.benchling.api;

import io.studytracker.benchling.api.entities.BenchlingEntryRequest;
import io.studytracker.benchling.api.entities.BenchlingEntryRequest.CustomField;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.eln.NotebookEntry;
import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.eln.NotebookUser;
import io.studytracker.eln.NotebookUserService;
import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public final class BenchlingNotebookEntryService
    extends AbstractBenchlingApiService
    implements NotebookEntryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookEntryService.class);

  @Autowired
  private NotebookUserService notebookUserService;

  @Override
  public List<NotebookTemplate> findEntryTemplates() {
    LOGGER.info("Fetching Benchling notebook entry templates.");
    String authHeader = generateAuthorizationHeader();
    List<BenchlingEntryTemplate> templates = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryTemplateList templateList = this.getClient().findEntryTemplates(authHeader, nextToken);
      templates.addAll(templateList.getEntryTemplates());
      nextToken = templateList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return this.convertNotebookEntryTemplates(templates);
  }

  @Override
  public List<NotebookTemplate> searchNotebookTemplates(String keyword) {
    return this.findEntryTemplates().stream()
        .filter(template -> template.getName().toLowerCase().contains(keyword.toLowerCase()))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<NotebookTemplate> findEntryTemplateById(String id) {
    LOGGER.info("Fetching Benchling notebook entry template: " + id);
    String authHeader = generateAuthorizationHeader();
    BenchlingEntryTemplate template = this.getClient().findEntryTemplateById(id, authHeader);
    return Optional.of(this.convertNotebookEntryTemplate(template));
  }

  @Override
  public NotebookEntry createStudyNotebookEntry(Study study) throws NotebookException {
    return this.createStudyNotebookEntry(study, null);
  }

  @Override
  public NotebookEntry createStudyNotebookEntry(Study study, NotebookTemplate template)
      throws NotebookException {

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(study.getCode() + " Study Summary: " + study.getName());
    request.setFolderId(study.getNotebookFolder().getReferenceId());

    // Users
    List<String> userIds = new ArrayList<>();
    for (User user : study.getUsers()) {
      Optional<NotebookUser> userOptional = notebookUserService.findNotebookUser(user);
      if (userOptional.isPresent()) {
        userIds.add(userOptional.get().getReferenceId());
      } else {
        LOGGER.warn("Could not find user registered in Benchling: " + user);
      }
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getReferenceId());
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("Name", new CustomField(study.getName()));
    customFields.put("Code", new CustomField(study.getCode()));
    customFields.put(
        "Description", new CustomField(study.getDescription().replaceAll("<.+?>", "")));
    customFields.put("Program", new CustomField(study.getProgram().getName()));
    if (study.getExternalCode() != null) {
      customFields.put("External Code", new CustomField(study.getExternalCode()));
    }
    request.setCustomFields(customFields);

    String authHeader = generateAuthorizationHeader();
    return this.convertBenchlingEntry(this.getClient().createEntry(request, authHeader));
  }

  @Override
  public NotebookEntry createAssayNotebookEntry(Assay assay) throws NotebookException {
    return this.createAssayNotebookEntry(assay, null);
  }

  @Override
  public NotebookEntry createAssayNotebookEntry(Assay assay, NotebookTemplate template)
      throws NotebookException {

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(assay.getCode() + " Assay Summary: " + assay.getName());
    request.setFolderId(assay.getNotebookFolder().getReferenceId());

    // Users
    List<String> userIds = new ArrayList<>();
    for (User user : assay.getUsers()) {
      Optional<NotebookUser> userOptional = notebookUserService.findNotebookUser(user);
      if (userOptional.isPresent()) {
        userIds.add(userOptional.get().getReferenceId());
      } else {
        LOGGER.warn("Could not find user registered in Benchling: " + user);
      }
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getReferenceId());
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("Name", new CustomField(assay.getName()));
    customFields.put("Code", new CustomField(assay.getCode()));
    customFields.put(
        "Description", new CustomField(assay.getDescription().replaceAll("<.+?>", "")));
    customFields.put("Assay Type", new CustomField(assay.getAssayType().getName()));
    customFields.put("Study", new CustomField(assay.getStudy().getCode()));
    request.setCustomFields(customFields);

    String authHeader = generateAuthorizationHeader();
    return this.convertBenchlingEntry(this.getClient().createEntry(request, authHeader));
  }


  private NotebookTemplate convertNotebookEntryTemplate(BenchlingEntryTemplate benchlingTemplate) {
    NotebookTemplate template = new NotebookTemplate();
    template.setName(benchlingTemplate.getName());
    template.setReferenceId(benchlingTemplate.getId());
    return template;
  }

  private List<NotebookTemplate> convertNotebookEntryTemplates(
      List<BenchlingEntryTemplate> benchlingEntryTemplates) {
    List<NotebookTemplate> templates = new ArrayList<>();
    for (BenchlingEntryTemplate benchlingEntryTemplate : benchlingEntryTemplates) {
      templates.add(this.convertNotebookEntryTemplate(benchlingEntryTemplate));
    }
    return templates;
  }


}
