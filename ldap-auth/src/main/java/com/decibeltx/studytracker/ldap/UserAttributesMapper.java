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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper;

public class UserAttributesMapper implements AttributesMapper<LdapUser> {

  @Override
  public LdapUser mapFromAttributes(Attributes attributes) throws NamingException {
    LdapUser user = new LdapUser();
    NamingEnumeration<? extends Attribute> enumeration = attributes.getAll();
    while (enumeration.hasMore()) {
      Attribute attribute = enumeration.next();
      switch (attribute.getID()) {
        case "displayName":
          user.setDisplayName((String) attribute.get());
          break;
        case "sAMAccountName":
          user.setUsername((String) attribute.get());
          break;
        case "mail":
          user.setEmail((String) attribute.get());
          break;
        default:
          user.getAttributes().put(attribute.getID(), attribute.get());
      }
    }
    return user;
  }

}
