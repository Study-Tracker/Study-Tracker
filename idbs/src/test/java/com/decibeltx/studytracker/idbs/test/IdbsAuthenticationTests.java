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

package com.decibeltx.studytracker.idbs.test;

import com.decibeltx.studytracker.idbs.inventory.InventoryRestApiAuthenticationService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles({"example"})
public class IdbsAuthenticationTests {

  @Autowired
  private InventoryRestApiAuthenticationService authenticationService;

  // Should be able to generate an authentication token. Re-authentication attempt should get you the same toke,
  // which has not yet expired.
  @Test
  public void loginTest() throws Exception {
    String token = null;
    Exception exception = null;
    try {
      token = authenticationService.getAuthenticationToken();
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(token);
    String token2 = null;
    try {
      token2 = authenticationService.getAuthenticationToken();
    } catch (Exception e) {
      e.printStackTrace();
      exception = e;
    }
    Assert.assertNull(exception);
    Assert.assertNotNull(token2);
    Assert.assertEquals(token, token2);
  }

}
