package com.decibeltx.studytracker.core.events.dto;

import com.decibeltx.studytracker.core.model.Assay;
import com.decibeltx.studytracker.core.model.Status;
import com.decibeltx.studytracker.core.model.Task;
import com.decibeltx.studytracker.core.model.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class AssayView {

  private String id;

  private Status status;

  private AssayTypeView assayType;

  private String study;

  private String name;

  private String code;

  private String description;

  private String createdBy;

  private String lastModifiedBy;

  private String owner;

  private Date startDate;

  private Date endDate;

  private boolean active;

  private Date createdAt;

  private Date updatedAt;

  private List<String> users = new ArrayList<>();

  private Map<String, Object> fields = new LinkedHashMap<>();

  private Map<String, String> attributes = new LinkedHashMap<>();

  private List<Task> tasks = new ArrayList<>();

  private AssayView() {
  }

  public static AssayView from(Assay assay) {
    AssayView view = new AssayView();
    view.setId(assay.getId());
    view.setName(assay.getName());
    view.setCode(assay.getCode());
    view.setStudy(assay.getStudy().getCode());
    view.setDescription(assay.getDescription());
    view.setStatus(assay.getStatus());
    view.setOwner(assay.getOwner().getDisplayName());
    view.setCreatedBy(assay.getCreatedBy().getDisplayName());
    view.setLastModifiedBy(assay.getLastModifiedBy().getDisplayName());
    view.setCreatedAt(assay.getCreatedAt());
    view.setUpdatedAt(assay.getUpdatedAt());
    view.setActive(assay.isActive());
    view.setStartDate(assay.getStartDate());
    view.setEndDate(assay.getEndDate());
    view.setUsers(assay.getUsers().stream()
        .map(User::getDisplayName)
        .collect(Collectors.toList()));
    view.setFields(assay.getFields());
    view.setTasks(assay.getTasks());
    view.setAttributes(assay.getAttributes());
    view.setAssayType(AssayTypeView.from(assay.getAssayType()));
    return view;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public AssayTypeView getAssayType() {
    return assayType;
  }

  public void setAssayType(AssayTypeView assayType) {
    this.assayType = assayType;
  }

  public String getStudy() {
    return study;
  }

  public void setStudy(String study) {
    this.study = study;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public void setFields(Map<String, Object> fields) {
    this.fields = fields;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }
}
