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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class LdapUserDetailsService implements UserDetailsService {

  @Autowired
  private LdapTemplate ldapTemplate;

  @Autowired
  private Environment env;

  @Override
  public LdapUser loadUserByUsername(String username) throws UsernameNotFoundException {
    LdapQuery query = LdapQueryBuilder.query()
        .searchScope(SearchScope.SUBTREE)
        .base(env.getRequiredProperty("ldap.searchBase"))
        .where("objectClass").is("User")
        .and("displayName").isPresent()
        .and("sAMAccountName").is(username);
    List<LdapUser> results = ldapTemplate.search(query, new UserAttributesMapper());
    if (results.size() == 0) {
      throw new UsernameNotFoundException(username);
    }
    return results.get(0);
  }

  public List<LdapUser> loadAllUsers() {
    LdapQuery query = LdapQueryBuilder.query()
        .searchScope(SearchScope.SUBTREE)
        .base("OU=Pliancy Users,OU=Users,OU=Decibel Corp")
        .where("objectClass")
        .is("User")
        .and("objectCategory")
        .is("person")
        .and("mail")
        .isPresent();
    return ldapTemplate.search(query, new UserAttributesMapper());
  }


}
