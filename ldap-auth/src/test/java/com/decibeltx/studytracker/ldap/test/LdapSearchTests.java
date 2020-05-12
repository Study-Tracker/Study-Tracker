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

package com.decibeltx.studytracker.ldap.test;

import com.decibeltx.studytracker.ldap.LdapUser;
import com.decibeltx.studytracker.ldap.UserAttributesMapper;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class LdapSearchTests {

  @Autowired
  private Environment env;

  @Autowired
  private LdapTemplate ldapTemplate;

  @Test
  public void ldapAuthenticationTest() throws Exception {
    ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
        env.getRequiredProperty("ldap.domain"),
        env.getRequiredProperty("ldap.url"),
        env.getRequiredProperty("ldap.base")
    );
    provider.setConvertSubErrorCodesToExceptions(true);
    provider.setUseAuthenticationRequestCredentials(true);
    provider.setSearchFilter(env.getRequiredProperty("ldap.filter"));
    Authentication authentication = provider
        .authenticate(new UsernamePasswordAuthenticationToken(env.getRequiredProperty(
            "ldap.example.username"), env.getRequiredProperty("ldap.example.password")));
    Assert.assertNotNull(authentication);
    System.out.println(authentication.toString());
  }

  @Test
  public void ldapSearchTest() throws Exception {
    LdapQuery query = LdapQueryBuilder.query()
        .searchScope(SearchScope.SUBTREE)
        .timeLimit(3000)
        .countLimit(10)
        .base(LdapUtils.emptyLdapName())
        .where("objectClass")
        .is("User")
        .and("displayName")
        .isPresent()
        .and("sAMAccountName")
        .is(env.getRequiredProperty("ldap.example.username"));
    List<LdapUser> results = ldapTemplate.search(query, new UserAttributesMapper());
    Assert.assertNotNull(results);
    Assert.assertEquals(1, results.size());
    LdapUser user = results.get(0);
    Assert.assertEquals("LDAP", user.getUsername());
  }

  @Test
  public void findAllUsersTest() throws Exception {
    LdapQuery query = LdapQueryBuilder.query()
        .searchScope(SearchScope.SUBTREE)
        .base("OU=Pliancy Users,OU=Users,OU=Decibel Corp")
        .where("objectClass")
        .is("User")
        .and("objectCategory")
        .is("person")
        .and("mail")
        .isPresent();
    List<LdapUser> results = ldapTemplate.search(query, new UserAttributesMapper());

    Assert.assertNotNull(results);
    for (LdapUser user : results) {
      System.out.println(user.getDisplayName() + " (" + user.getUsername() + ")");
    }
  }

}
