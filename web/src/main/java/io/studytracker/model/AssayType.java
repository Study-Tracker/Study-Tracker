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

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_types")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "assay-type-details",
      attributeNodes = {
          @NamedAttributeNode("fields"),
          @NamedAttributeNode(value = "tasks", subgraph = "assay-type-task-details")
      },
      subgraphs = {
          @NamedSubgraph(
              name = "assay-type-task-details",
              attributeNodes = {@NamedAttributeNode("fields")}
          )
      })
})
public class AssayType extends CustomEntity {

  @OneToMany(
      mappedBy = "assayType",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<AssayTypeField> fields = new HashSet<>();

  @OneToMany(
      mappedBy = "assayType",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private Set<AssayTypeTask> tasks = new HashSet<>();

  @Type(JsonBinaryType.class)
  @Column(name = "attributes", columnDefinition = "json")
  private Map<String, String> attributes = new HashMap<>();

  public void addTask(AssayTypeTask task) {
    task.setAssayType(this);
    this.tasks.add(task);
  }

  public void removeTask(AssayTypeTask task) {
    this.tasks.remove(task);
  }

  public void removeTask(Long id) {
    this.tasks.removeIf(t -> t.getId().equals(id));
  }

  public void addField(AssayTypeField field) {
    field.setAssayType(this);
    this.fields.add(field);
  }

  public void removeField(AssayTypeField field) {
    this.fields.remove(field);
  }

  public void removeField(Long id) {
    this.fields.removeIf(f -> f.getId().equals(id));
  }

  public void addAttribute(String key, String value) {
    this.attributes.put(key, value);
  }

  public void removeAttribute(String key) {
    this.attributes.remove(key);
  }

  public Set<AssayTypeField> getFields() {
    return fields;
  }

  public void setFields(Set<AssayTypeField> fields) {
    for (AssayTypeField field : fields) {
      field.setAssayType(this);
    }
    this.fields = fields;
  }

  public Set<AssayTypeTask> getTasks() {
    return tasks;
  }

  public void setTasks(Set<AssayTypeTask> tasks) {
    for (AssayTypeTask task : tasks) {
      task.setAssayType(this);
    }
    this.tasks = tasks;
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }

  @Override
  public String toString() {
    return "AssayType{" +
        "fields=" + (fields != null ? fields.toString() : "[...]") +
        ", tasks=" + (tasks != null ? tasks.toString() : "[...]") +
        ", attributes=" + attributes +
        '}';
  }
}
