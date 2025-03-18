/*
 * Copyright 2019-2023 the original author or authors.
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

import io.studytracker.config.properties.ApplicationProperties;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Encrypts and decrypts fields in the database.
 * From https://sultanov.dev/blog/database-column-level-encryption-with-spring-data-jpa/
 */
@Component
@Converter(autoApply = true)
public class StringFieldEncryptor implements AttributeConverter<String, String>,
    ApplicationContextAware {

  private static final String AES = "AES"; // "AES/CBC/PKCS5Padding";
//  private Key key;
  private static ApplicationContext applicationContext;

  // No-argument constructor required by JPA
  public StringFieldEncryptor() {
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    StringFieldEncryptor.applicationContext = applicationContext;
  }

  // Helper method to fetch the secret from ApplicationProperties on demand.
  private String getSecret() {
    ApplicationProperties properties = applicationContext.getBean(ApplicationProperties.class);
    return properties.getSecret();
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) return null;
    try {
      String secret = getSecret();
      Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), AES);
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException |
             NoSuchPaddingException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    try {
      String secret = getSecret();
      Key key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), AES);
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, key);
//      byte[] decodedBytes = Base64.getDecoder().decode(dbData.trim());
//      byte[] decryptedBytes = cipher.doFinal(decodedBytes);
//      return new String(decryptedBytes, StandardCharsets.UTF_8);
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData.trim())));
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
             NoSuchPaddingException e) {
      throw new IllegalStateException(e);
    }
  }
}
