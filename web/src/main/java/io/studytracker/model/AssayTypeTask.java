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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_type_tasks")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "assay-type-task-details",
        attributeNodes = {@NamedAttributeNode("fields")})
})
public class AssayTypeTask extends Task {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_type_id", nullable = false)
  private AssayType assayType;

  @OneToMany(
      mappedBy = "assayTypeTask",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<AssayTypeTaskField> fields = new HashSet<>();

  public AssayType getAssayType() {
    return assayType;
  }

  public void setAssayType(AssayType assayType) {
    this.assayType = assayType;
  }

  public Set<AssayTypeTaskField> getFields() {
    return fields;
  }

  public void setFields(Set<AssayTypeTaskField> fields) {
    for (AssayTypeTaskField field: fields) {
      field.setAssayTypeTask(this);
    }
    this.fields = fields;
  }

  public void addField(AssayTypeTaskField field) {
    this.fields.add(field);
    field.setAssayTypeTask(this);
  }

  public void addFields(Collection<AssayTypeTaskField> assayTypeTaskFields) {
    for (AssayTypeTaskField f: assayTypeTaskFields) {
      this.addField(f);
    }
  }

  @Override
  public String toString() {
    return "AssayTypeTask{" +
        "id=" + this.getId() +
        ", label=" + this.getLabel() +
        ", order=" + this.getOrder() +
        ", createdAt=" + this.getCreatedAt() +
        ", updatedAt=" + this.getUpdatedAt() +
        ", status=" + this.getStatus() +
        ", fields=" + (fields != null ? fields.toString() : "[...]") +
        '}';
  }
}
