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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;

@Entity
@Table(name = "study_relationships")
@NamedEntityGraphs({
    @NamedEntityGraph(name = "relationship-details", attributeNodes = {
        @NamedAttributeNode("sourceStudy"),
        @NamedAttributeNode("targetStudy")
    })
})
public class StudyRelationship {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private RelationshipType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_study_id", nullable = false)
  private Study sourceStudy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_study_id", nullable = false)
  private Study targetStudy;

  public StudyRelationship() {
  }

  public StudyRelationship(RelationshipType type, Study sourceStudy, Study targetStudy) {
    this.type = type;
    this.sourceStudy = sourceStudy;
    this.targetStudy = targetStudy;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public RelationshipType getType() {
    return type;
  }

  public void setType(RelationshipType type) {
    this.type = type;
  }

  public Study getSourceStudy() {
    return sourceStudy;
  }

  public void setSourceStudy(Study sourceStudy) {
    this.sourceStudy = sourceStudy;
  }

  public Study getTargetStudy() {
    return targetStudy;
  }

  public void setTargetStudy(Study targetStudy) {
    this.targetStudy = targetStudy;
  }
}
