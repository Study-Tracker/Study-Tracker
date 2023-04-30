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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "gitlab_repositories", uniqueConstraints = {
    @UniqueConstraint(name = "uq_gitlab_repositories", columnNames = {"gitlab_group_id", "repository_id"})
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class GitLabRepository {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public GitLabGroup getGitLabGroup() {
    return gitLabGroup;
  }

  public void setGitLabGroup(GitLabGroup gitLabGroup) {
    this.gitLabGroup = gitLabGroup;
  }

  public GitRepository getGitRepository() {
    return gitRepository;
  }

  public void setGitRepository(GitRepository gitRepository) {
    this.gitRepository = gitRepository;
  }

  public Integer getRepositoryId() {
    return repositoryId;
  }

  public void setRepositoryId(Integer repositoryId) {
    this.repositoryId = repositoryId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
