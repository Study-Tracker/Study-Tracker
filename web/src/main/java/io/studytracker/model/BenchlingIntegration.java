/*
 * Copyright 2019-2025 the original author or authors.
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
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
