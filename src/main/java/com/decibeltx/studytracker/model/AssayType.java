/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_types")
@EntityListeners(AuditingEntityListener.class)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "assay-type-details", attributeNodes = {
        @NamedAttributeNode("fields"),
        @NamedAttributeNode("tasks")
    })
})
public class AssayType extends CustomEntity {

  @OneToMany(mappedBy = "assayType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<AssayTypeField> fields = new HashSet<>();

  @OneToMany(mappedBy = "assayType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  private Set<AssayTypeTask> tasks = new HashSet<>();

  @Type(type = "json")
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
    for (AssayTypeField field: fields) {
      field.setAssayType(this);
    }
    this.fields = fields;
  }

  public Set<AssayTypeTask> getTasks() {
    return tasks;
  }

  public void setTasks(Set<AssayTypeTask> tasks) {
    for (AssayTypeTask task :tasks) {
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
}
