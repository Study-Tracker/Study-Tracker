package io.studytracker.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "integration_instance_configuration_values", uniqueConstraints = {
    @UniqueConstraint(name = "uc_integrationinstanceconfigurationvalue", columnNames = {
        "integration_instance_id", "field_name"})
})
public class IntegrationInstanceConfigurationValue implements Model {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "integration_instance_id", nullable = false, updatable = false)
  private IntegrationInstance integrationInstance;

  @Column(name = "field_name", nullable = false)
  private String fieldName;

  @Column(name = "value", nullable = false, length = 1024)
  @Convert(converter = StringFieldEncryptor.class)
  private String value;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IntegrationInstance getIntegrationInstance() {
    return integrationInstance;
  }

  public void setIntegrationInstance(IntegrationInstance integrationInstance) {
    this.integrationInstance = integrationInstance;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
