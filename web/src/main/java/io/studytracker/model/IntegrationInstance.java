/*
 * Copyright 2022 the original author or authors.
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

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a single implementation of a {@link SupportedIntegration}, capturing the configuration
 *   properties and display name of the integration. This record can be updated with new configuration
 *   if a new version of the configuration schema is published.
 *
 * @author williamoemler
 * @since 0.7.1
 */

@Entity
@Table(name = "integration_instances")
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
@EntityListeners(AuditingEntityListener.class)
public class IntegrationInstance implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "supported_integration_id", nullable = false)
  private SupportedIntegration supportedIntegration;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "name", nullable = false, updatable = false)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "configuration", columnDefinition = "json")
  @Type(type = "json")
  private Map<String, Object> configuration = new LinkedHashMap<>();

  @CreatedDate
  @Column(name = "created_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public SupportedIntegration getSupportedIntegration() {
    return supportedIntegration;
  }

  public void setSupportedIntegration(SupportedIntegration supportedIntegration) {
    this.supportedIntegration = supportedIntegration;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Map<String, Object> getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Map<String, Object> configuration) {
    this.configuration = configuration;
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

  public Optional<Object> findConfigurationValue(String key) {
    return Optional.ofNullable(configuration.get(key));
  }

}
