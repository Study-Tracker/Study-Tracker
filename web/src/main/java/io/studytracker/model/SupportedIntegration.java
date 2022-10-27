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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Type;
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
@Table(name = "supported_integrations",
    uniqueConstraints = {
      @UniqueConstraint(name = "uc_supportedintegration_name", columnNames = {"name", "version"})
    }
)
@TypeDef(name = "json", typeClass = JsonBinaryType.class)
public class SupportedIntegration implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "active", nullable = false)
  private boolean active = true;

  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "configuration_schema", columnDefinition = "json")
  @Type(type = "json")
  private Map<String, Object> configurationSchema = new LinkedHashMap<>();

  @OneToMany(
      mappedBy = "supportedIntegration",
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

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Map<String, Object> getConfigurationSchema() {
    return configurationSchema;
  }

  public void setConfigurationSchema(Map<String, Object> configurationSchema) {
    this.configurationSchema = configurationSchema;
  }
}
