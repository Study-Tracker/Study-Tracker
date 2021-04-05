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

import java.util.Date;
import lombok.Data;

@Data
public class Task {

  private TaskStatus status;

  private String label;

  private Integer order;

  private Date createdAt = new Date();

  private Date updatedAt = new Date();

  public enum TaskStatus {
    TODO, COMPLETE, INCOMPLETE
  }

  public Task() {
  }

  public Task(String label) {
    this.status = TaskStatus.TODO;
    this.label = label;
    this.order = 0;
  }

  public Task(String label, TaskStatus status) {
    this.status = status;
    this.label = label;
    this.order = 0;
  }

  public Task(String label, TaskStatus status, Integer order) {
    this.status = status;
    this.label = label;
    this.order = order;
  }

}
