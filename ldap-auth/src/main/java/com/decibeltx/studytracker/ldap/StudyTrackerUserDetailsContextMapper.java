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

import com.decibeltx.studytracker.core.exception.StudyTrackerException;
import java.util.ArrayList;
import java.util.Collection;
import javax.naming.NamingException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

public class StudyTrackerUserDetailsContextMapper extends LdapUserDetailsMapper {

  @Override
  public UserDetails mapUserFromContext(
      DirContextOperations context, String s, Collection<? extends GrantedAuthority> authorities
  ) {
    LdapUser user;
    UserDetails userDetails = super.mapUserFromContext(context, s, authorities);
    UserAttributesMapper mapper = new UserAttributesMapper();
    try {
      user = mapper.mapFromAttributes(context.getAttributes());
    } catch (NamingException e) {
      throw new StudyTrackerException(e);
    }
    user.setAuthorities(new ArrayList<>(authorities));
    user.setEnabled(userDetails.isEnabled());
    user.setAccountNonExpired(userDetails.isAccountNonExpired());
    user.setAccountNonLocked(userDetails.isAccountNonLocked());
    user.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
    return user;
  }

  @Override
  public void mapUserToContext(
      UserDetails userDetails, DirContextAdapter dirContextAdapter
  ) {
  }

}
