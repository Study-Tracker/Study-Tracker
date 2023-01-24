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

package io.studytracker.security;

import static org.passay.AllowedCharacterRule.ERROR_CODE;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordGenerator {

  private static final int PASSWORD_LENGTH = 8;

  private final PasswordGenerator passwordGenerator = new PasswordGenerator();

  private CharacterRule[] getCharacterRules() {
    CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
    lowerCaseRule.setNumberOfCharacters(2);
    CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
    upperCaseRule.setNumberOfCharacters(1);
    CharacterRule numberRule = new CharacterRule(EnglishCharacterData.Digit);
    numberRule.setNumberOfCharacters(1);
    CharacterData specialChars =
        new CharacterData() {
          public String getErrorCode() {
            return ERROR_CODE;
          }

          public String getCharacters() {
            return "!@#$%^&*()_+";
          }
        };
    CharacterRule specialCharacterRule = new CharacterRule(specialChars);
    specialCharacterRule.setNumberOfCharacters(1);
    return new CharacterRule[] {lowerCaseRule, upperCaseRule, numberRule, specialCharacterRule};
  }

  public String generatePassword() {
    return this.generatePassword(PASSWORD_LENGTH);
  }

  public String generatePassword(int length) {
    return passwordGenerator.generatePassword(length, getCharacterRules());
  }
}
