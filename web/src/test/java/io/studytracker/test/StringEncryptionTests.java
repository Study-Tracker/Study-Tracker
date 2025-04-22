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
import io.studytracker.model.StringFieldEncryptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
public class StringEncryptionTests {

  @Autowired(required = false)
  private StringFieldEncryptor stringFieldEncryptor;

  @Test
  public void configTest() {
    Assert.assertNotNull(stringFieldEncryptor);
  }

  @Test
  public void encryptDecryptTest() {
    String original = "Hello, world!";
    String encrypted = stringFieldEncryptor.convertToDatabaseColumn(original);
    System.out.println("Encrypted: " + encrypted);
    String decrypted = stringFieldEncryptor.convertToEntityAttribute(encrypted);
    System.out.println("Decrypted: " + decrypted);
    Assert.assertEquals(original, decrypted);
  }

}
