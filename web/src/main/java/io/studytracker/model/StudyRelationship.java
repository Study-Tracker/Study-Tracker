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
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "study_relationships")
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = "relationship-details",
      attributeNodes = {@NamedAttributeNode("sourceStudy"), @NamedAttributeNode("targetStudy")})
})
public class StudyRelationship extends Model {

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

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private RelationshipType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "source_study_id", nullable = false)
  private Study sourceStudy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "target_study_id", nullable = false)
  private Study targetStudy;

  public StudyRelationship() {}

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
