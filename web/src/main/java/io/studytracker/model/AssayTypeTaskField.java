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

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "assay_type_task_fields")
@EntityListeners(AuditingEntityListener.class)
public class AssayTypeTaskField extends CustomEntityField {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assay_type_task_id", nullable = false)
  private AssayTypeTask assayTypeTask;

  public AssayTypeTask getAssayTypeTask() {
    return assayTypeTask;
  }

  public void setAssayTypeTask(AssayTypeTask assayTypeTask) {
    this.assayTypeTask = assayTypeTask;
  }

  @Override
  public String toString() {
    return "AssayTypeTaskField{" +
        "id=" + this.getId() +
        ", displayName='" + this.getDisplayName() + '\'' +
        ", fieldName='" + this.getFieldName() + '\'' +
        ", type=" + this.getType() +
        ", required=" + this.isRequired() +
        ", description='" + this.getDescription() + '\'' +
        ", active=" + this.isActive() +
        ", fieldOrder=" + this.getFieldOrder() +
//        ", assayTypeTask=" + assayTypeTask +
        '}';
  }
}
