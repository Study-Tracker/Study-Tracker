package io.studytracker.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Defines a field that captures configuration properties for a {@link SupportedIntegration}.
 *
 * @author Will Oemler
 * @since 0.7.1
 */

@Entity
@Table(name = "integration_configuration_schema_fields")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationConfigurationSchemaField extends CustomEntityField {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supported_integration_id", nullable = false)
  private SupportedIntegration supportedIntegration;

  public IntegrationConfigurationSchemaField() {
    super();
  }

  public SupportedIntegration getSupportedIntegration() {
    return supportedIntegration;
  }

  public void setSupportedIntegration(SupportedIntegration supportedIntegration) {
    this.supportedIntegration = supportedIntegration;
  }
}
