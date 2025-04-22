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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_tasks")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "assay-task-details",
      attributeNodes = {
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModifiedBy"),
        @NamedAttributeNode("assignedTo"),
        @NamedAttributeNode("fields")
      })
})
@Getter
@Setter
public class AssayTask extends Task {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_id", nullable = false)
  private Assay assay;

  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @LastModifiedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by", nullable = false)
  private User lastModifiedBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assigned_to")
  private User assignedTo;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "due_date")
  private Date dueDate;

  @OneToMany(
      mappedBy = "assayTask",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<AssayTaskField> fields = new HashSet<>();

  @Type(JsonBinaryType.class)
  @Column(name = "data", columnDefinition = "json")
  private Map<String, Object> data = new LinkedHashMap<>();

  @JsonIgnore
  public Assay getAssay() {
    return assay;
  }

  public void setFields(Set<AssayTaskField> fields) {
    for (AssayTaskField field: fields) {
      field.setAssayTask(this);
    }
    this.fields = fields;
  }

  public void addField(AssayTaskField field) {
    this.fields.add(field);
    field.setAssayTask(this);
  }

  public void addFields(Collection<AssayTaskField> assayTaskFields) {
    for (AssayTaskField f: assayTaskFields) {
      this.addField(f);
    }
  }

  public void addData(String key, Object value) {
    this.data.put(key, value);
  }

}
