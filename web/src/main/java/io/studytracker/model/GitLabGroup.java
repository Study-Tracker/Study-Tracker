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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Table(name = "gitlab_groups", uniqueConstraints = {
    @UniqueConstraint(name = "uq_gitlab_groups", columnNames = {"gitlab_integration_id", "path"})
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class GitLabGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "gitlab_integration_id", nullable = false)
  private GitLabIntegration gitLabIntegration;

  @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "git_group_id", nullable = false)
  private GitGroup gitGroup;

  @Column(name = "group_id", nullable = false, updatable = false)
  private Integer groupId;

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

  public GitLabIntegration getGitLabIntegration() {
    return gitLabIntegration;
  }

  public void setGitLabIntegration(GitLabIntegration gitLabIntegration) {
    this.gitLabIntegration = gitLabIntegration;
  }

  public GitGroup getGitGroup() {
    return gitGroup;
  }

  public void setGitGroup(GitGroup gitGroup) {
    this.gitGroup = gitGroup;
  }

  public Integer getGroupId() {
    return groupId;
  }

  public void setGroupId(Integer groupId) {
    this.groupId = groupId;
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
