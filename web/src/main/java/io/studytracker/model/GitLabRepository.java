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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "gitlab_repositories", uniqueConstraints = {
    @UniqueConstraint(name = "uq_gitlab_repositories", columnNames = {"gitlab_group_id", "repository_id"})
})
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class GitLabRepository {

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

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "gitlab_group_id", nullable = false)
  private GitLabGroup gitLabGroup;

  @OneToOne(targetEntity = GitRepository.class, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "git_repository_id", nullable = false)
  private GitRepository gitRepository;

  @Column(name = "repository_id", nullable = false, updatable = false)
  private Integer repositoryId;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false, length = 1024)
  private String path;

}
