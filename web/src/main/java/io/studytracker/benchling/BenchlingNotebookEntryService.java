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
import io.studytracker.benchling.api.entities.BenchlingEntryRequest.Field;
import io.studytracker.benchling.api.entities.BenchlingEntrySchema;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplate;
import io.studytracker.benchling.api.entities.BenchlingEntryTemplateList;
import io.studytracker.eln.NotebookEntry;
import io.studytracker.eln.NotebookEntryService;
import io.studytracker.eln.NotebookTemplate;
import io.studytracker.eln.NotebookUser;
import io.studytracker.eln.NotebookUserService;
import io.studytracker.exception.NotebookException;
import io.studytracker.model.Assay;
import io.studytracker.model.AssayOptions;
import io.studytracker.model.ELNFolder;
import io.studytracker.model.Study;
import io.studytracker.model.StudyOptions;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public final class BenchlingNotebookEntryService
    extends AbstractBenchlingApiService
    implements NotebookEntryService<ELNFolder> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BenchlingNotebookEntryService.class);

  @Autowired
  private NotebookUserService notebookUserService;

  @Override
  public List<NotebookTemplate> findEntryTemplates() {
    LOGGER.info("Fetching Benchling notebook entry templates.");
    BenchlingElnRestClient client = this.getClient();
    List<NotebookTemplate> templates = new ArrayList<>();
    String nextToken = null;
    boolean hasNext = true;
    while (hasNext) {
      BenchlingEntryTemplateList templateList = client.findEntryTemplates(nextToken);
      templates.addAll(templateList.getEntryTemplates());
      nextToken = templateList.getNextToken();
      hasNext = StringUtils.hasText(nextToken);
    }
    return templates;
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
    if (template.getSchema() != null && StringUtils.hasText(template.getSchema().getId())) {
      BenchlingEntrySchema schema = client.findEntrySchemaById(template.getSchema().getId())
          .orElse(null);
      if (schema != null) {
        template.setSchema(schema);
      }
    }
    return Optional.of(template);
  }


  @Override
  public NotebookEntry createStudyNotebookEntry(Study study, ELNFolder studyFolder,
      NotebookTemplate template) throws NotebookException {

    StudyOptions options = study.getOptions();

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(study.getCode() + " Study Summary: " + study.getName());
    request.setFolderId(studyFolder.getReferenceId());

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
    if (userIds.isEmpty()) {
      throw new NotebookException("No registered Benchling users found for authoring entry");
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getId());
      if (((BenchlingEntryTemplate) template).getSchema() != null) {
        request.setSchemaId(((BenchlingEntryTemplate) template).getSchema().getId());
      }
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("StudyTracker.Name", new CustomField(study.getName()));
    customFields.put("StudyTracker.Code", new CustomField(study.getCode()));
    customFields.put("StudyTracker.Description",
        new CustomField(study.getDescription().replaceAll("<.+?>", "")));
    customFields.put("StudyTracker.Program", new CustomField(study.getProgram().getName()));
    if (study.getExternalCode() != null) {
      customFields.put("StudyTracker.ExternalCode", new CustomField(study.getExternalCode()));
    }
    request.setCustomFields(customFields);

    // Template fields
    if (options.getNotebookTemplateFields() != null
        && !options.getNotebookTemplateFields().isEmpty()) {
      for (Map.Entry<String, Object> entry : options.getNotebookTemplateFields().entrySet()) {
        request.addField(entry.getKey(), entry.getValue());
      }
    }
    
    BenchlingElnRestClient client = this.getClient();
    return this.convertBenchlingEntry(client.createEntry(request));
  }

  @Override
  public NotebookEntry createAssayNotebookEntry(Assay assay, ELNFolder folder, NotebookTemplate template)
      throws NotebookException {

    AssayOptions options = assay.getOptions();

    BenchlingEntryRequest request = new BenchlingEntryRequest();
    request.setName(assay.getCode() + " Assay Summary: " + assay.getName());
    request.setFolderId(folder.getReferenceId());

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
    if (userIds.isEmpty()) {
      throw new NotebookException("No registered Benchling users found for authoring entry");
    }
    request.setAuthorIds(userIds);

    // Entry template
    if (template != null) {
      request.setEntryTemplateId(template.getId());
      if (((BenchlingEntryTemplate) template).getSchema() != null) {
        request.setSchemaId(((BenchlingEntryTemplate) template).getSchema().getId());
      }
    }

    // Custom fields
    Map<String, CustomField> customFields = new LinkedHashMap<>();
    customFields.put("StudyTracker.Name", new CustomField(assay.getName()));
    customFields.put("StudyTracker.Code", new CustomField(assay.getCode()));
    customFields.put("StudyTracker.Description",
        new CustomField(assay.getDescription().replaceAll("<.+?>", "")));
    customFields.put("StudyTracker.AssayType", new CustomField(assay.getAssayType().getName()));
    customFields.put("StudyTracker.Study", new CustomField(assay.getStudy().getCode()));
    request.setCustomFields(customFields);

    // Template fields
    if (options.getNotebookTemplateFields() != null
        && !options.getNotebookTemplateFields().isEmpty()) {
      for (Map.Entry<String, Object> entry : options.getNotebookTemplateFields().entrySet()) {
        request.addField(entry.getKey(), entry.getValue());
      }
    }
    
    BenchlingElnRestClient client = this.getClient();
    return this.convertBenchlingEntry(client.createEntry(request));
  }

}
