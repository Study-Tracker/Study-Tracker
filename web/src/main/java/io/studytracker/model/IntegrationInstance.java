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

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Represents a single implementation of a {@link IntegrationDefinition}, capturing the configuration
 *   properties and display name of the integration. This record can be updated with new configuration
 *   if a new version of the configuration schema is published.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Deprecated
@Entity
@Table(name = "integration_instances")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationInstance implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "integration_definition_id", nullable = false)
  private IntegrationDefinition definition;

  @Column(name = "display_name", unique = true, nullable = false)
  private String displayName;

  @Column(name = "name", unique = true, nullable = false, updatable = false)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @OneToMany(mappedBy = "integrationInstance", cascade = CascadeType.ALL,
      orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<IntegrationInstanceConfigurationValue> configurationValues = new HashSet<>();

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

  public IntegrationDefinition getDefinition() {
    return definition;
  }

  public void setDefinition(IntegrationDefinition integrationDefinition) {
    this.definition = integrationDefinition;
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

  public Set<IntegrationInstanceConfigurationValue> getConfigurationValues() {
    return configurationValues;
  }

  public void setConfigurationValues(
      Set<IntegrationInstanceConfigurationValue> configurationValues) {
    this.configurationValues = configurationValues;
  }

  public void addConfigurationValue(IntegrationInstanceConfigurationValue configurationValue) {
    configurationValue.setIntegrationInstance(this);
    configurationValues.add(configurationValue);
  }

  public Optional<String> getConfigurationValue(String key) {
    return configurationValues.stream()
        .filter(v -> v.getFieldName().equals(key))
        .findFirst()
        .map(IntegrationInstanceConfigurationValue::getValue);
  }

  public boolean hasConfigurationValue(String key) {
    return configurationValues.stream()
        .anyMatch(v -> v.getFieldName().equals(key));
  }
}
