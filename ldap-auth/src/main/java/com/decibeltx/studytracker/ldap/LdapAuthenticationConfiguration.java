/*
 * Copyright 2020 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decibeltx.studytracker.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(name = "security.mode", havingValue = "ldap")
public class LdapAuthenticationConfiguration {

  @Autowired
  private Environment env;

  @Bean
  public AuthenticationProvider ldapAuthenticationProvider() {
    Assert.notNull(env.getProperty("ldap.domain"), "LDAP domain is not set.");
    Assert.notNull(env.getProperty("ldap.url"), "LDAP server URL is not set.");
    Assert.notNull(env.getProperty("ldap.base"), "LDAP search base is not set.");
    Assert.notNull(env.getProperty("ldap.filter"), "LDAP search filter is not set.");
    ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
        env.getRequiredProperty("ldap.domain"),
        env.getRequiredProperty("ldap.url"),
        env.getRequiredProperty("ldap.base")
    );
    provider.setConvertSubErrorCodesToExceptions(true);
    provider.setUseAuthenticationRequestCredentials(true);
    provider.setSearchFilter(env.getRequiredProperty("ldap.filter"));
    provider.setUserDetailsContextMapper(studyTrackerUserDetailsMapper());
    return provider;
  }

  @Bean
  public LdapContextSource ldapContextSource() {
    Assert.notNull(env.getProperty("ldap.url"), "LDAP server URL is not set. Eg. ldap.url=");
    Assert.notNull(env.getProperty("ldap.base"), "LDAP search base is not set.");
    Assert.notNull(env.getProperty("ldap.admin.username"), "LDAP admin username is not set.");
    Assert.notNull(env.getProperty("ldap.admin.password"), "LDAP admin password is not set.");
    LdapContextSource contextSource = new LdapContextSource();
    contextSource.setUrl(env.getRequiredProperty("ldap.url"));
    contextSource.setBase(env.getRequiredProperty("ldap.base"));
    contextSource.setUserDn(env.getRequiredProperty("ldap.admin.username"));
    contextSource.setPassword(env.getRequiredProperty("ldap.admin.password"));
    return contextSource;
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource());
    ldapTemplate.setIgnorePartialResultException(true);
    return ldapTemplate;
  }

  @Bean
  @ConditionalOnMissingBean(UserDetailsService.class)
  public LdapUserDetailsService ldapUserDetailsService() {
    return new LdapUserDetailsService();
  }

  @Bean
  public UserDetailsContextMapper studyTrackerUserDetailsMapper() {
    return new StudyTrackerUserDetailsContextMapper();
  }

  @Bean
  public LdapUserRepositoryPopulator ldapUserRepositoryPopulator() {
    return new LdapUserRepositoryPopulator();
  }

}
