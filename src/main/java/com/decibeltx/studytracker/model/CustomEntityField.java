package com.decibeltx.studytracker.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class CustomEntityField {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "field_name", nullable = false)
  private String fieldName;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private CustomEntityFieldType type;

  @Column(name = "required", nullable = false)
  private boolean required;

  @Column(name = "description", length = 1024)
  private String description;

  @Column(name = "active", nullable = false)
  private boolean active;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public CustomEntityFieldType getType() {
    return type;
  }

  public void setType(CustomEntityFieldType type) {
    this.type = type;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
