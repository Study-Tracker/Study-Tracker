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

package io.studytracker.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class CustomEntityField extends Model {

  @Id
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "hibernate_sequence"
  )
  @SequenceGenerator(
      name = "hibernate_sequence",
      allocationSize = 1
  )
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

  @Column(name = "field_order", nullable = false)
  private Integer fieldOrder;

  @Column(name = "dropdown_options", length = 2048)
  private String dropdownOptions;

  @Column(name = "default_value")
  private String defaultValue;

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

  public Integer getFieldOrder() {
    return fieldOrder;
  }

  public void setFieldOrder(Integer fieldOrder) {
    this.fieldOrder = fieldOrder;
  }

  public String getDropdownOptions() {
    return dropdownOptions;
  }

  public void setDropdownOptions(String dropdownOptions) {
    this.dropdownOptions = dropdownOptions;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @Override
  public String toString() {
    return "CustomEntityField{" +
        "id=" + id +
        ", displayName='" + displayName + '\'' +
        ", fieldName='" + fieldName + '\'' +
        ", type=" + type +
        ", required=" + required +
        ", description='" + description + '\'' +
        ", active=" + active +
        ", fieldOrder=" + fieldOrder +
        ", dropdownOptions=" + dropdownOptions +
        ", defaultValue=" + defaultValue +
        '}';
  }
}
