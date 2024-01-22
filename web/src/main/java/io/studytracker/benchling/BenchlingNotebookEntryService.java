/*
 * Copyright 2019-2023 the original author or authors.
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

package io.studytracker.benchling;

import io.studytracker.benchling.api.AbstractBenchlingApiService;
import io.studytracker.benchling.api.BenchlingElnRestClient;
import io.studytracker.benchling.api.entities.BenchlingEntryRequest;
import io.studytracker.benchling.api.entities.BenchlingEntryRequest.CustomField;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.eln.*;
import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.Study;
import io.studytracker.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class BenchlingNotebookEntryService
    extends AbstractBenchlingApiService
    implements NotebookEntryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookEntryService.class);

  @Autowired
  private NotebookUserService notebookUserService;

  @Override
  public List<NotebookTemplate> findEntryTemplates() {
    LOGGER.info("Fetching Benchling notebook entry templates.");
    BenchlingElnRestClient client = this.getClient();
    List<BenchlingEntryTemplate> templates = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryTemplateList templateList = client.findEntryTemplates(nextToken);
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
    BenchlingElnRestClient client = this.getClient();
    BenchlingEntryTemplate template = client.findEntryTemplateById(id);
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
    
    BenchlingElnRestClient client = this.getClient();
    return this.convertBenchlingEntry(client.createEntry(request));
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
        LOGGER.warn("Could not find user registered in Benchling: " + user.getUsername());
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
    
    BenchlingElnRestClient client = this.getClient();
    return this.convertBenchlingEntry(client.createEntry(request));
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
