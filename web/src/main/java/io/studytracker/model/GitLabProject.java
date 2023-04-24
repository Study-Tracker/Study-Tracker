package io.studytracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "gitlab_projects")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class GitLabProject {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "gitlab_group_id", nullable = false)
  private GitLabGroup gitLabGroup;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
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
