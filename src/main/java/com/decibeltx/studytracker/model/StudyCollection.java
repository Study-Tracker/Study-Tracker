package com.decibeltx.studytracker.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "study_collections")
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
    @NamedEntityGraph(name = "study-collection-summary",
        attributeNodes = {
            @NamedAttributeNode("createdBy")
        }
    ),
    @NamedEntityGraph(name = "study-collection-details", attributeNodes = {
        @NamedAttributeNode("createdBy"),
        @NamedAttributeNode("lastModifiedBy"),
        @NamedAttributeNode("studies")
    })
})
public class StudyCollection {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "shared")
  private boolean shared = false;

  @CreatedBy
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "createdBy", nullable = false, updatable = false)
  private User createdBy;

  @LastModifiedBy
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "last_modified_by", nullable = false)
  private User lastModifiedBy;

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "study_collection_studies",
      joinColumns = @JoinColumn(name = "study_collection_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "study_id", nullable = false))
  private Set<Study> studies = new HashSet<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isShared() {
    return shared;
  }

  public void setShared(boolean shared) {
    this.shared = shared;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public User getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(User lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<Study> getStudies() {
    return studies;
  }

  public void setStudies(Set<Study> studies) {
    this.studies = studies;
  }

  public void addStudy(Study study) {
    this.studies.add(study);
  }

  public void removeStudy(Study study) {
    this.studies.removeIf(d -> d.getId().equals(study.getId()));
  }

}
