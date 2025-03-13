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
import jakarta.crypto.BadPaddingException;
import jakarta.crypto.Cipher;
import jakarta.crypto.IllegalBlockSizeException;
import jakarta.crypto.spec.SecretKeySpec;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Encrypts and decrypts fields in the database.
 * From https://sultanov.dev/blog/database-column-level-encryption-with-spring-data-jpa/
 */
@Component
@Converter
public class StringFieldEncryptor implements AttributeConverter<String, String> {

  private static final String AES = "AES";

  private final Key key;
  private final Cipher cipher;

  @Autowired
  public StringFieldEncryptor(ApplicationProperties properties) throws Exception {
    key = new SecretKeySpec(properties.getSecret().getBytes(), AES);
    cipher = Cipher.getInstance(AES);
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, key);
      if (attribute == null) return null;
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, key);
      if (dbData == null) return null;
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new IllegalStateException(e);
    }
  }
}
