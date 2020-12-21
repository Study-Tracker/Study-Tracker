package com.decibeltx.studytracker.core.events.dto;

import com.decibeltx.studytracker.core.model.AssayType;
import com.decibeltx.studytracker.core.model.AssayTypeField;
import com.decibeltx.studytracker.core.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AssayTypeView {

  private String id;

  private String name;

  private boolean active;

  private List<AssayTypeField> fields = new ArrayList<>();

  private List<Task> tasks = new ArrayList<>();

  private Map<String, String> attributes = new HashMap<>();

  private AssayTypeView() {
  }

  public static AssayTypeView from(AssayType assayType) {
    AssayTypeView view = new AssayTypeView();
    view.setId(assayType.getId());
    view.setName(assayType.getName());
    view.setActive(assayType.isActive());
    view.setFields(assayType.getFields());
    view.setTasks(assayType.getTasks());
    view.setAttributes(assayType.getAttributes());
    return view;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public List<AssayTypeField> getFields() {
    return fields;
  }

  public void setFields(List<AssayTypeField> fields) {
    this.fields = fields;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public void setTasks(List<Task> tasks) {
    this.tasks = tasks;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
}
