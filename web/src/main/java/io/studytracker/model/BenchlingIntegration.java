package io.studytracker.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "benchling_integrations")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
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
    
    @Column(name = "active", nullable = false)
    private boolean active;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
}
