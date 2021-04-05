package com.decibeltx.studytracker.events.dto;

import com.decibeltx.studytracker.model.Status;
import com.decibeltx.studytracker.model.Study;
import com.decibeltx.studytracker.model.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class StudyView {

  private String id;

  private String name;

  private String code;

  private String externalCode;

  private String program;

  private String description;

  private String collaborator;

  private Status status;

  private String owner;

  private String createdBy;

  private String lastModifiedBy;

  private Date startDate;

  private Date endDate;

  private Date createdAt;

  private Date updatedAt;

  private boolean legacy;

  private boolean active;

  private List<KeywordView> keywords = new ArrayList<>();

  private List<String> users = new ArrayList<>();

  private Map<String, Object> attributes = new HashMap<>();

  private StudyView() {
  }

  public static StudyView from(Study study) {
    StudyView view = new StudyView();
    view.setId(study.getId());
    view.setName(study.getName());
    view.setCode(study.getCode());
    view.setExternalCode(study.getExternalCode());
    view.setProgram(study.getProgram().getName());
    view.setDescription(study.getDescription());
    view.setStatus(study.getStatus());
    view.setOwner(study.getOwner().getDisplayName());
    view.setCreatedBy(study.getCreatedBy().getDisplayName());
    view.setLastModifiedBy(study.getLastModifiedBy().getDisplayName());
    view.setCreatedAt(study.getCreatedAt());
    view.setUpdatedAt(study.getUpdatedAt());
    view.setLegacy(study.isLegacy());
    view.setActive(study.isActive());
    view.setKeywords(study.getKeywords().stream()
        .map(KeywordView::from)
        .collect(Collectors.toList()));
    view.setStartDate(study.getStartDate());
    view.setEndDate(study.getEndDate());
    view.setUsers(study.getUsers().stream()
        .map(User::getDisplayName)
        .collect(Collectors.toList()));
    view.setAttributes(study.getAttributes());
    if (study.getCollaborator() != null) {
      view.setCollaborator(study.getCollaborator().getLabel());
    }
    return view;
  }

  public String getId() {
    return id;
  }

  private void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  private void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  private void setCode(String code) {
    this.code = code;
  }

  public String getExternalCode() {
    return externalCode;
  }

  private void setExternalCode(String externalCode) {
    this.externalCode = externalCode;
  }

  public String getProgram() {
    return program;
  }

  private void setProgram(String program) {
    this.program = program;
  }

  public String getDescription() {
    return description;
  }

  private void setDescription(String description) {
    this.description = description;
  }

  public Status getStatus() {
    return status;
  }

  private void setStatus(Status status) {
    this.status = status;
  }

  public String getOwner() {
    return owner;
  }

  private void setOwner(String owner) {
    this.owner = owner;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  private void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  private void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  private void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  private void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean isLegacy() {
    return legacy;
  }

  private void setLegacy(boolean legacy) {
    this.legacy = legacy;
  }

  public boolean isActive() {
    return active;
  }

  private void setActive(boolean active) {
    this.active = active;
  }

  public List<KeywordView> getKeywords() {
    return keywords;
  }

  private void setKeywords(List<KeywordView> keywords) {
    this.keywords = keywords;
  }

  public List<String> getUsers() {
    return users;
  }

  private void setUsers(List<String> users) {
    this.users = users;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  private void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public String getCollaborator() {
    return collaborator;
  }

  private void setCollaborator(String collaborator) {
    this.collaborator = collaborator;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
}
