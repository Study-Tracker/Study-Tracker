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
import io.studytracker.integration.IntegrationType;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.TypeDef;

/**
 * Defines a supported integration with an external system or service. Should capture the required
 *   field that are needed to configure the integration. The {@link IntegrationInstance} entity
 *   implements the integration and captures the actual configuration. Configuration schemas should
 *   be versioned with the {@link #version} field. Out-of-date configuration schemas should have
 *   their {@link #active} field set to false.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Entity
@Table(name = "integration_definitions",
    uniqueConstraints = {
      @UniqueConstraint(name = "uc_integrationdefinition_name", columnNames = {"type", "version"})
    }
)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class IntegrationDefinition implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "type", nullable = false)
  @Enumerated(EnumType.STRING)
  private IntegrationType type;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "version", nullable = false)
  private Integer version;

  @OneToMany(mappedBy = "integrationDefinition", cascade = CascadeType.ALL,
      fetch = FetchType.EAGER, orphanRemoval = true)
  private Set<IntegrationConfigurationSchemaField> configurationSchemaFields = new HashSet<>();

  @OneToMany(
      mappedBy = "definition",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<IntegrationInstance> instances = new HashSet<>();

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IntegrationType getType() {
    return type;
  }

  public void setType(IntegrationType name) {
    this.type = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Set<IntegrationConfigurationSchemaField> getConfigurationSchemaFields() {
    return configurationSchemaFields;
  }

  public void setConfigurationSchemaFields(
      Set<IntegrationConfigurationSchemaField> configurationSchemaFields) {
    this.configurationSchemaFields = configurationSchemaFields;
  }

  public Set<IntegrationInstance> getInstances() {
    return instances;
  }

  public void setInstances(Set<IntegrationInstance> instances) {
    this.instances = instances;
  }

  public void addConfigurationSchemaField(IntegrationConfigurationSchemaField field) {
    configurationSchemaFields.add(field);
    field.setIntegrationDefinition(this);
  }
}
