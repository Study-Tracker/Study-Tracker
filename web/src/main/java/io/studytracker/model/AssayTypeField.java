/*
 * Copyright 2022 the original author or authors.
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

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_type_fields")
@EntityListeners(AuditingEntityListener.class)
public class AssayTypeField extends CustomEntityField {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_type_id", nullable = false)
  private AssayType assayType;

  public AssayTypeField() {
    super();
  }

  public AssayTypeField(
      AssayType assayType,
      String displayName,
      String fieldName,
      CustomEntityFieldType type,
      Integer order
  ) {
    super();
    this.setAssayType(assayType);
    this.setDisplayName(displayName);
    this.setFieldName(fieldName);
    this.setType(type);
    this.setRequired(false);
    this.setActive(true);
    this.setFieldOrder(order);
  }

  public AssayTypeField(
      AssayType assayType,
      String displayName,
      String fieldName,
      CustomEntityFieldType type,
      Integer order,
      boolean required) {
    this.setAssayType(assayType);
    this.setDisplayName(displayName);
    this.setFieldName(fieldName);
    this.setType(type);
    this.setFieldOrder(order);
    this.setRequired(required);
    this.setActive(true);
  }

  public AssayTypeField(
      AssayType assayType,
      String displayName,
      String fieldName,
      CustomEntityFieldType type,
      Integer order,
      boolean required,
      String description) {
    this.setAssayType(assayType);
    this.setDisplayName(displayName);
    this.setFieldName(fieldName);
    this.setType(type);
    this.setRequired(required);
    this.setFieldOrder(order);
    this.setActive(true);
    this.setDescription(description);
  }

  public AssayType getAssayType() {
    return assayType;
  }

  public void setAssayType(AssayType assayType) {
    this.assayType = assayType;
  }

  @Override
  public String toString() {
    return "AssayTypeField{" +
        "id=" + this.getId() +
        ", displayName='" + this.getDisplayName() + '\'' +
        ", fieldName='" + this.getFieldName() + '\'' +
        ", type=" + this.getType() +
        ", required=" + this.isRequired() +
        ", description='" + this.getDescription() + '\'' +
        ", active=" + this.isActive() +
        ", fieldOrder=" + this.getFieldOrder() +
        ", assayType=" + assayType +
        '}';
  }
}
