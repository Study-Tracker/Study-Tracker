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
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "collaborators")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Collaborator extends Model {

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

  @Column(name = "label", unique = true, nullable = false)
  private String label;

  @Column(name = "organization_name", nullable = false)
  private String organizationName;

  @Column(name = "organization_location")
  private String organizationLocation;

  @Column(name = "contact_person_name")
  private String contactPersonName;

  @Column(name = "contact_email")
  private String contactEmail;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "active", nullable = false)
  private boolean active = true;

}
