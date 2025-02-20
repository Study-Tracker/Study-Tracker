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

package io.studytracker.test;

import io.studytracker.Application;
import io.studytracker.config.HostInformation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public class ConfigurationTests {

  @Autowired
  private ApplicationContext context;

  @Test
  public void contextLoads() {
    Assert.assertNotNull(context);
  }

  @Test
  public void hostInformationTest() {
    HostInformation hostInformation = context.getBean(HostInformation.class);
    Assert.assertNotNull(hostInformation);
    System.out.println(hostInformation.toString());
    Assert.assertEquals("localhost", hostInformation.getHostName());
    Assert.assertFalse(hostInformation.getJavaVersion().startsWith("@"));
    Assert.assertFalse(hostInformation.getApplicationVersion().startsWith("@"));
    Assert.assertEquals("http://localhost:0", hostInformation.getApplicationUrl());
  }

}
