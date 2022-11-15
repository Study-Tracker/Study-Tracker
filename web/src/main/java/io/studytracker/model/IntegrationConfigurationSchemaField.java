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

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Defines a field that captures configuration properties for a {@link IntegrationDefinition}.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Entity
@Table(name = "integration_configuration_schema_fields")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationConfigurationSchemaField extends CustomEntityField {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "integration_definition_id", nullable = false)
  private IntegrationDefinition integrationDefinition;

  public IntegrationConfigurationSchemaField() {
    super();
  }

  public IntegrationDefinition getIntegrationDefinition() {
    return integrationDefinition;
  }

  public void setIntegrationDefinition(IntegrationDefinition integrationDefinition) {
    this.integrationDefinition = integrationDefinition;
  }
}
