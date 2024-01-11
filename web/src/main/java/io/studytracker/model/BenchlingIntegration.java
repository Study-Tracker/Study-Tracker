package io.studytracker.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Table(name = "benchling_integrations")
@EntityListeners(AuditingEntityListener.class)
public class BenchlingIntegration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "tenant_name", nullable = false)
    private String tenantName;
    
    @Column(name = "root_url", nullable = false)
    private String rootUrl;
    
    @Column(name = "client_id")
    @Convert(converter = StringFieldEncryptor.class)
    private String clientId;
    
    @Column(name = "client_secret", length = 1024)
    @Convert(converter = StringFieldEncryptor.class)
    private String clientSecret;
    
    @Column(name = "username")
    @Convert(converter = StringFieldEncryptor.class)
    private String username;
    
    @Column(name = "password")
    @Convert(converter = StringFieldEncryptor.class)
    private String password;
    
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
    
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public String getRootUrl() {
        return rootUrl;
    }
    
    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
